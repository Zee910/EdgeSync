package com.nexis.edgesync.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nexis.edgesync.viewmodel.EdgeSyncViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: EdgeSyncViewModel) {
    val mqtt = viewModel.mqttStats.value
    val http = viewModel.httpStats.value
    val ws = viewModel.wsStats.value
    val running = viewModel.isRunning.value
    val hasData = (mqtt.successCount + http.successCount + ws.successCount) > 0

    val configuration = LocalConfiguration.current
    val isWide = configuration.screenWidthDp.dp > 640.dp
    val cards = listOf(mqtt, http, ws)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("EdgeSync", fontWeight = FontWeight.Bold)
                        Text(
                            "IoT protocol benchmark",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { if (running) viewModel.stopBenchmark() else viewModel.startBenchmark() },
                icon = { Icon(if (running) Icons.Filled.Stop else Icons.Filled.PlayArrow, contentDescription = null) },
                text = { Text(if (running) "Stop" else "Start Benchmark") },
                containerColor = if (running) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primary,
                contentColor = if (running) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimary,
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .padding(bottom = 88.dp)
        ) {
            AnimatedVisibility(visible = !hasData) {
                EmptyState()
            }

            if (isWide) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    cards.forEach { stats ->
                        ProtocolCard(stats, Modifier.weight(1f))
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    cards.forEach { stats -> ProtocolCard(stats) }
                }
            }

            Spacer(Modifier.height(24.dp))
            Text("Comparison", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            ResultsTable(rows = cards)
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun EmptyState() {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("3 virtual smart plugs \u00B7 3 sync protocols", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))
            Text(
                "Tap \u201CStart Benchmark\u201D to race MQTT, HTTP polling, and WebSocket head-to-head on latency, reliability, and battery impact.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
        }
    }
}
