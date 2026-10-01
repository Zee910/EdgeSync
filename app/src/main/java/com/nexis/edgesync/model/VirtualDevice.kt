package com.nexis.edgesync.model

/**
 * Simulated smart plug — no real hardware needed.
 * Each protocol implementation toggles this device's state
 * and we time how long the round-trip takes.
 */
data class VirtualDevice(
    val id: String,
    val name: String,
    var state: Boolean = false,
    var lastSyncTime: Long = 0L,
    var latency: Long = 0L
)

enum class Protocol { MQTT, HTTP, WEBSOCKET }

/**
 * Rolling stats for one protocol, shown on its dashboard card.
 */
data class ProtocolStats(
    val protocol: Protocol,
    val latencySamples: MutableList<Long> = mutableListOf(),
    var successCount: Int = 0,
    var failureCount: Int = 0,
    var isConnected: Boolean = false
) {
    val avgLatencyMs: Long
        get() = if (latencySamples.isEmpty()) 0L else latencySamples.average().toLong()

    val reliabilityPercent: Double
        get() {
            val total = successCount + failureCount
            return if (total == 0) 100.0 else (successCount * 100.0) / total
        }

    fun recordSuccess(latencyMs: Long) {
        latencySamples.add(latencyMs)
        if (latencySamples.size > 50) latencySamples.removeAt(0) // keep it bounded
        successCount++
    }

    fun recordFailure() {
        failureCount++
    }
}
