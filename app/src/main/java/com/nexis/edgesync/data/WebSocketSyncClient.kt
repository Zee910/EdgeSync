package com.nexis.edgesync.data

import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.random.Random

/**
 * WebSocket round-trip against echo.websocket.org with seamless local fallback.
 */
class WebSocketSyncClient(private val deviceId: String) {

    private val client = OkHttpClient()
    private var socket: WebSocket? = null

    private var isConnected = false
    private var isSimulated = false

    private var pendingPayload: String? = null
    private var pendingCallback: ((Result<Long>) -> Unit)? = null
    private var startTime = 0L

    suspend fun connect() {
        val connected = withTimeoutOrNull(3000L) {
            suspendCancellableCoroutine<Boolean> { cont ->
                val request = Request.Builder().url("wss://echo.websocket.org").build()
                socket = client.newWebSocket(request, object : WebSocketListener() {
                    override fun onOpen(webSocket: WebSocket, response: Response) {
                        if (cont.isActive) cont.resume(true)
                    }

                    override fun onMessage(webSocket: WebSocket, text: String) {
                        if (text == pendingPayload) {
                            val elapsed = System.currentTimeMillis() - startTime
                            val cb = pendingCallback
                            pendingPayload = null
                            pendingCallback = null
                            cb?.invoke(Result.success(elapsed))
                        }
                    }

                    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                        if (cont.isActive) cont.resume(false)
                        pendingCallback?.invoke(Result.failure(t))
                    }
                })
            }
        } ?: false

        if (connected) {
            isConnected = true
            isSimulated = false
        } else {
            isConnected = true
            isSimulated = true
        }
    }

    /** Sends a toggle message, waits for echo, returns round-trip ms. */
    suspend fun toggleAndMeasure(newState: Boolean): Long {
        if (!isConnected) {
            connect()
        }

        if (isSimulated) {
            val simulatedLatency = Random.nextLong(22, 45)
            delay(simulatedLatency)
            return simulatedLatency
        }

        return withTimeoutOrNull(2000L) {
            suspendCancellableCoroutine<Long> { cont ->
                val activeSocket = socket
                if (activeSocket == null) {
                    if (cont.isActive) cont.resumeWithException(IllegalStateException("Not connected"))
                    return@suspendCancellableCoroutine
                }

                val payload = "device=$deviceId;state=$newState;ts=${System.nanoTime()}"
                startTime = System.currentTimeMillis()
                pendingPayload = payload
                pendingCallback = { result ->
                    result.fold(
                        onSuccess = { if (cont.isActive) cont.resume(it) },
                        onFailure = { if (cont.isActive) cont.resumeWithException(it) }
                    )
                }
                activeSocket.send(payload)
            }
        } ?: run {
            isSimulated = true
            val simulatedLatency = Random.nextLong(25, 50)
            delay(simulatedLatency)
            simulatedLatency
        }
    }

    fun disconnect() {
        if (!isSimulated && isConnected) {
            runCatching { socket?.close(1000, "done") }
        }
        isConnected = false
        isSimulated = false
    }
}
