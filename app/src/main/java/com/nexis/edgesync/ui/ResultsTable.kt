package com.nexis.edgesync.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nexis.edgesync.model.Protocol
import com.nexis.edgesync.model.ProtocolStats
import com.nexis.edgesync.ui.theme.HttpColor
import com.nexis.edgesync.ui.theme.MqttColor
import com.nexis.edgesync.ui.theme.WebSocketColor

private fun batteryImpactFor(protocol: Protocol): String = when (protocol) {
    Protocol.MQTT -> "Low"        // persistent connection, event-driven
    Protocol.HTTP -> "High"       // wakes radio every poll
    Protocol.WEBSOCKET -> "Medium" // persistent, but no broker-side batching
}

private fun colorFor(protocol: Protocol) = when (protocol) {
    Protocol.MQTT -> MqttColor
    Protocol.HTTP -> HttpColor
    Protocol.WEBSOCKET -> WebSocketColor
}

@Composable
fun ResultsTable(rows: List<ProtocolStats>, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(vertical = 8.dp)) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                HeaderCell("Protocol", 1.2f)
                HeaderCell("Latency", 1f)
                HeaderCell("Reliability", 1f)
                HeaderCell("Battery", 0.9f)
            }
            rows.forEachIndexed { index, stats ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(
                            if (index % 2 == 0) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
                            else MaterialTheme.colorScheme.surface
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(modifier = Modifier.weight(1.2f), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Box(
                            Modifier
                                .size(8.dp)
                                .background(colorFor(stats.protocol), RoundedCornerShape(4.dp))
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            stats.protocol.name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Text("${stats.avgLatencyMs}ms", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                    Text("%.1f%%".format(stats.reliabilityPercent), modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                    Text(batteryImpactFor(stats.protocol), modifier = Modifier.weight(0.9f), style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun RowScope.HeaderCell(text: String, weight: Float) {
    Text(
        text,
        modifier = Modifier.weight(weight),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = FontWeight.SemiBold
    )
}
