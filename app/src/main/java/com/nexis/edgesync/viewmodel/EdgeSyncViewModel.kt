package com.nexis.edgesync.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nexis.edgesync.data.HttpSyncClient
import com.nexis.edgesync.data.MqttSyncClient
import com.nexis.edgesync.data.WebSocketSyncClient
import com.nexis.edgesync.data.local.AppDatabase
import com.nexis.edgesync.data.local.BenchmarkRunEntity
import com.nexis.edgesync.model.Protocol
import com.nexis.edgesync.model.ProtocolStats
import com.nexis.edgesync.model.VirtualDevice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EdgeSyncViewModel(application: Application) : AndroidViewModel(application) {

    val devices = listOf(
        VirtualDevice(id = "plug_1", name = "Smart Plug 1"),
        VirtualDevice(id = "plug_2", name = "Smart Plug 2"),
        VirtualDevice(id = "plug_3", name = "Smart Plug 3"),
    )

    val mqttStats = mutableStateOf(ProtocolStats(Protocol.MQTT))
    val httpStats = mutableStateOf(ProtocolStats(Protocol.HTTP))
    val wsStats = mutableStateOf(ProtocolStats(Protocol.WEBSOCKET))

    val isRunning = mutableStateOf(false)

    private val mqttClient = MqttSyncClient(devices[0].id)
    private val httpClient = HttpSyncClient()
    private val wsClient = WebSocketSyncClient(devices[2].id)

    /** Starts all three protocol benchmark loops instantly in parallel. */
    fun startBenchmark() {
        if (isRunning.value) return
        isRunning.value = true

        // Mark all protocols connected instantly on start
        mqttStats.value = mqttStats.value.copy(isConnected = true)
        httpStats.value = httpStats.value.copy(isConnected = true)
        wsStats.value = wsStats.value.copy(isConnected = true)

        viewModelScope.launch(Dispatchers.IO) {
            // Launch connection attempts asynchronously without blocking loop
            launch { runCatching { mqttClient.connect() } }
            launch { runCatching { wsClient.connect() } }

            // Immediate first cycle
            launch { runMqttCycle() }
            launch { runWebSocketCycle() }
            launch { runHttpCycle() }

            // Continuous telemetry loop every 2 seconds
            while (isRunning.value) {
                delay(2000)
                if (!isRunning.value) break
                launch { runMqttCycle() }
                launch { runWebSocketCycle() }
                launch { runHttpCycle() }
            }
        }
    }

    fun stopBenchmark() {
        if (!isRunning.value) return
        isRunning.value = false
        mqttClient.disconnect()
        wsClient.disconnect()

        val currentMqtt = mqttStats.value
        val currentHttp = httpStats.value
        val currentWs = wsStats.value

        viewModelScope.launch(Dispatchers.IO) {
            val sessionId = System.currentTimeMillis()
            val timestamp = System.currentTimeMillis()
            val dao = AppDatabase.getInstance(getApplication()).benchmarkDao()

            val runs = listOf(
                currentMqtt.toEntity(sessionId, timestamp),
                currentHttp.toEntity(sessionId, timestamp),
                currentWs.toEntity(sessionId, timestamp),
            ).filter { (it.successCount > 0) || (it.failureCount > 0) }

            if (runs.isNotEmpty()) {
                dao.insertAll(runs)
            }
        }
    }

    private fun ProtocolStats.toEntity(sessionId: Long, timestamp: Long) = BenchmarkRunEntity(
        sessionId = sessionId,
        timestamp = timestamp,
        protocol = protocol.name,
        avgLatencyMs = avgLatencyMs,
        reliabilityPercent = reliabilityPercent,
        successCount = successCount,
        failureCount = failureCount,
    )

    private suspend fun runMqttCycle() {
        val stats = mqttStats.value
        runCatching {
            val newState = !devices[0].state
            val latency = mqttClient.toggleAndMeasure(newState)
            devices[0].state = newState
            devices[0].latency = latency
            stats.recordSuccess(latency)
            stats.isConnected = true
        }.onFailure {
            stats.recordFailure()
            stats.isConnected = false
        }
        mqttStats.value = stats.copy()
    }

    private suspend fun runHttpCycle() {
        val stats = httpStats.value
        withContext(Dispatchers.IO) {
            runCatching {
                val newState = !devices[1].state
                val latency = httpClient.toggleAndMeasure(devices[1].id, newState)
                devices[1].state = newState
                devices[1].latency = latency
                stats.recordSuccess(latency)
                stats.isConnected = true
            }.onFailure {
                stats.recordFailure()
                stats.isConnected = false
            }
        }
        httpStats.value = stats.copy()
    }

    private suspend fun runWebSocketCycle() {
        val stats = wsStats.value
        runCatching {
            val newState = !devices[2].state
            val latency = wsClient.toggleAndMeasure(newState)
            devices[2].state = newState
            devices[2].latency = latency
            stats.recordSuccess(latency)
            stats.isConnected = true
        }.onFailure {
            stats.recordFailure()
            stats.isConnected = false
        }
        wsStats.value = stats.copy()
    }

    override fun onCleared() {
        stopBenchmark()
        super.onCleared()
    }
}
