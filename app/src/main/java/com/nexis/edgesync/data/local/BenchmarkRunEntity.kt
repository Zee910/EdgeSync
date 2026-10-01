package com.nexis.edgesync.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "benchmark_runs")
data class BenchmarkRunEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val timestamp: Long,
    val protocol: String,
    val avgLatencyMs: Long,
    val reliabilityPercent: Double,
    val successCount: Int,
    val failureCount: Int
)
