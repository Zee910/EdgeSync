package com.nexis.edgesync.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nexis.edgesync.data.local.AppDatabase
import com.nexis.edgesync.data.local.BenchmarkRunEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class BenchmarkSession(
    val sessionId: Long,
    val timestamp: Long,
    val rows: List<BenchmarkRunEntity>
)

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getInstance(application).benchmarkDao()

    private val _sessions = MutableStateFlow<List<BenchmarkSession>>(emptyList())
    val sessions: StateFlow<List<BenchmarkSession>> = _sessions

    init {
        viewModelScope.launch {
            dao.observeAll().collect { rows ->
                _sessions.value = rows
                    .groupBy { it.sessionId }
                    .map { (sessionId, group) ->
                        BenchmarkSession(
                            sessionId = sessionId,
                            timestamp = group.first().timestamp,
                            rows = group
                        )
                    }
                    .sortedByDescending { it.sessionId }
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch { dao.clearAll() }
    }
}
