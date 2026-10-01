package com.nexis.edgesync.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.PowerOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nexis.edgesync.model.Protocol
import com.nexis.edgesync.ui.theme.HttpColor
import com.nexis.edgesync.ui.theme.MqttColor
import com.nexis.edgesync.ui.theme.WebSocketColor
import com.nexis.edgesync.viewmodel.EdgeSyncViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevicesScreen(viewModel: EdgeSyncViewModel) {
    val devices = viewModel.devices
    val mqttStats = viewModel.mqttStats.value
    val httpStats = viewModel.httpStats.value
    val wsStats = viewModel.wsStats.value

    val protocolMap = mapOf(
        devices[0].id to Protocol.MQTT,
        devices[1].id to Protocol.HTTP,
        devices[2].id to Protocol.WEBSOCKET
    )

    val statsMap = mapOf(
        Protocol.MQTT to mqttStats,
        Protocol.HTTP to httpStats,
        Protocol.WEBSOCKET to wsStats
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Virtual Devices", fontWeight = FontWeight.Bold)
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(devices) { device ->
                val protocol = protocolMap[device.id] ?: Protocol.HTTP
                val stats = statsMap[protocol]
                val accent = when (protocol) {
                    Protocol.MQTT -> MqttColor
                    Protocol.HTTP -> HttpColor
                    Protocol.WEBSOCKET -> WebSocketColor
                }

                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(if (device.state) accent.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (device.state) Icons.Default.Power else Icons.Default.PowerOff,
                                contentDescription = null,
                                tint = if (device.state) accent else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(device.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("ID: ${device.id}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(4.dp))
                            SuggestionChip(
                                onClick = {},
                                label = { Text(protocol.name, style = MaterialTheme.typography.labelSmall) }
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                if (device.state) "ON" else "OFF",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (device.state) accent else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (stats != null && stats.isConnected) {
                                Text(
                                    "${device.latency} ms",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                Text(
                                    "Idle",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
