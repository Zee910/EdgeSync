package com.nexis.edgesync.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nexis.edgesync.ui.theme.HttpColor
import com.nexis.edgesync.ui.theme.MqttColor
import com.nexis.edgesync.ui.theme.WebSocketColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProtocolsScreen() {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Protocol Overview", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProtocolDetailCard(
                title = "MQTT (Message Queuing Telemetry Transport)",
                icon = Icons.Default.CloudSync,
                accent = MqttColor,
                architecture = "Publish / Subscribe over persistent TCP",
                latency = "Ultra-low (~10 - 30ms)",
                battery = "Minimal - Event driven, low overhead",
                description = "Lightweight messaging protocol designed for constrained IoT devices and low-bandwidth, high-latency networks."
            )

            ProtocolDetailCard(
                title = "HTTP Polling (REST API)",
                icon = Icons.Default.Bolt,
                accent = HttpColor,
                architecture = "Request / Response (Polled every 2s)",
                latency = "High (Depends on poll interval ~200ms+)",
                battery = "High - Constantly wakes cellular/Wi-Fi radio",
                description = "Standard REST API approach where client periodically requests device state from server."
            )

            ProtocolDetailCard(
                title = "WebSocket",
                icon = Icons.Default.Wifi,
                accent = WebSocketColor,
                architecture = "Full-Duplex over single TCP connection",
                latency = "Low (~20 - 50ms)",
                battery = "Medium - Keeps TCP connection open",
                description = "Bi-directional real-time communication channel enabling instantaneous updates without HTTP polling overhead."
            )
        }
    }
}

@Composable
private fun ProtocolDetailCard(
    title: String,
    icon: ImageVector,
    accent: androidx.compose.ui.graphics.Color,
    architecture: String,
    latency: String,
    battery: String,
    description: String
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(accent.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = accent)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(description, style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(8.dp))

            DetailRow("Architecture", architecture)
            DetailRow("Latency Profile", latency)
            DetailRow("Battery Impact", battery)
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            value,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}
