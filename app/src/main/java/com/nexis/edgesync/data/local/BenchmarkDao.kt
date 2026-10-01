package com.nexis.edgesync.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BenchmarkDao {

    @Insert
    suspend fun insertAll(runs: List<BenchmarkRunEntity>)

    @Query("SELECT * FROM benchmark_runs ORDER BY sessionId DESC, protocol ASC")
    fun observeAll(): Flow<List<BenchmarkRunEntity>>

    @Query("DELETE FROM benchmark_runs")
    suspend fun clearAll()
}
