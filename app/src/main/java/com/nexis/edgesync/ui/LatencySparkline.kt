package com.nexis.edgesync.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/**
 * Draws the last N latency samples as a smooth sparkline. Pure Canvas —
 * no chart dependency, no jitpack repo needed, renders instantly.
 */
@Composable
fun LatencySparkline(
    samples: List<Long>,
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        if (samples.size < 2) {
            drawLine(
                color = color.copy(alpha = 0.25f),
                start = Offset(0f, size.height / 2f),
                end = Offset(size.width, size.height / 2f),
                strokeWidth = 2f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
            )
            return@Canvas
        }

        val maxVal = (samples.maxOrNull() ?: 1L).coerceAtLeast(1L).toFloat()
        val minVal = (samples.minOrNull() ?: 0L).toFloat()
        val range = (maxVal - minVal).coerceAtLeast(1f)
        val stepX = size.width / (samples.size - 1).coerceAtLeast(1)

        val linePath = Path()
        val fillPath = Path()

        samples.forEachIndexed { index, value ->
            val x = index * stepX
            val normalized = (value.toFloat() - minVal) / range
            val y = size.height - (normalized * size.height * 0.85f) - (size.height * 0.1f)

            if (index == 0) {
                linePath.moveTo(x, y)
                fillPath.moveTo(x, size.height)
                fillPath.lineTo(x, y)
            } else {
                linePath.lineTo(x, y)
                fillPath.lineTo(x, y)
            }
        }
        fillPath.lineTo(size.width, size.height)
        fillPath.close()

        drawPath(fillPath, color = color.copy(alpha = 0.12f))
        drawPath(linePath, color = color, style = Stroke(width = 4f))

        val lastX = (samples.size - 1) * stepX
        val lastNorm = (samples.last().toFloat() - minVal) / range
        val lastY = size.height - (lastNorm * size.height * 0.85f) - (size.height * 0.1f)
        drawCircle(color = color, radius = 6f, center = Offset(lastX, lastY))
    }
}
