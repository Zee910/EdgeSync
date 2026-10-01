package com.nexis.edgesync.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nexis.edgesync.model.Protocol
import com.nexis.edgesync.model.ProtocolStats

private fun iconFor(protocol: Protocol) = when (protocol) {
    Protocol.MQTT -> Icons.Filled.CloudSync
    Protocol.HTTP -> Icons.Filled.Bolt
    Protocol.WEBSOCKET -> Icons.Filled.Wifi
}

private fun colorFor(protocol: Protocol) = when (protocol) {
    Protocol.MQTT -> com.nexis.edgesync.ui.theme.MqttColor
    Protocol.HTTP -> com.nexis.edgesync.ui.theme.HttpColor
    Protocol.WEBSOCKET -> com.nexis.edgesync.ui.theme.WebSocketColor
}

private fun subtitleFor(protocol: Protocol) = when (protocol) {
    Protocol.MQTT -> "Pub/Sub \u00B7 persistent"
    Protocol.HTTP -> "REST poll \u00B7 every 2s"
    Protocol.WEBSOCKET -> "Full-duplex \u00B7 persistent"
}

@Composable
private fun PulsingDot(color: Color, active: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (active) 1.6f else 1f,
        animationSpec = infiniteRepeatable(tween(900, easing = LinearEasing), RepeatMode.Reverse),
        label = "pulseScale"
    )
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(16.dp)) {
        if (active) {
            Box(
                Modifier
                    .size(16.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.25f))
            )
        }
        Box(
            Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(if (active) color else Color.Gray.copy(alpha = 0.4f))
        )
    }
}

@Composable
fun ProtocolCard(stats: ProtocolStats, modifier: Modifier = Modifier) {
    val accent = colorFor(stats.protocol)

    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.elevatedCardColors()
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .border(1.dp, accent.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                .padding(18.dp)
        ) {
            // Header row
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Box(
                    Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(accent.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(iconFor(stats.protocol), contentDescription = null, tint = accent)
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(stats.protocol.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(
                        subtitleFor(stats.protocol),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                PulsingDot(color = if (stats.isConnected) accent else Color.Gray, active = stats.isConnected)
            }

            Spacer(Modifier.height(16.dp))

            // Latency + reliability row
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Avg Latency", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        "${stats.avgLatencyMs} ms",
                        style = MaterialTheme.typography.headlineSmall,
                        color = accent
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Reliability", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        "%.1f%%".format(stats.reliabilityPercent),
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }

            Spacer(Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { (stats.reliabilityPercent / 100.0).toFloat().coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = accent,
                trackColor = accent.copy(alpha = 0.12f)
            )

            Spacer(Modifier.height(14.dp))
            LatencySparkline(samples = stats.latencySamples, color = accent)

            Spacer(Modifier.height(8.dp))
            Text(
                "${stats.successCount} ok \u00B7 ${stats.failureCount} failed",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
