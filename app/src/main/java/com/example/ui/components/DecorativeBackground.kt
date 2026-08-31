package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun DecorativeBackground(
    backgroundStyle: String = "Subtle Paper",
    isDark: Boolean = false,
    content: @Composable () -> Unit
) {
    val bgColor = MaterialTheme.colorScheme.background

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        when (backgroundStyle) {
            "Subtle Paper" -> {
                // Draws subtle warm parchment grain/fibers
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val lineColor = if (isDark) Color(0x0CFFFFFF) else Color(0x0E8C7E6A)
                    val step = 32.dp.toPx()
                    var y = 0f
                    while (y < size.height) {
                        drawLine(
                            color = lineColor,
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 0.6f
                        )
                        y += step
                    }
                }
            }

            "Minimal Grid" -> {
                // Clean subtle drafting grid
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val gridColor = if (isDark) Color(0x0DFFFFFF) else Color(0x0D4A3F35)
                    val gridSize = 24.dp.toPx()
                    var x = 0f
                    while (x < size.width) {
                        drawLine(
                            color = gridColor,
                            start = Offset(x, 0f),
                            end = Offset(x, size.height),
                            strokeWidth = 0.5f
                        )
                        x += gridSize
                    }
                    var y = 0f
                    while (y < size.height) {
                        drawLine(
                            color = gridColor,
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 0.5f
                        )
                        y += gridSize
                    }
                }
            }

            "Cinema Doodles" -> {
                // Subtle film strip perforations and tiny decorative stars along borders
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val doodleColor = if (isDark) Color(0x18FFFFFF) else Color(0x18E67E22)
                    val perfWidth = 8.dp.toPx()
                    val perfHeight = 5.dp.toPx()
                    val spacing = 18.dp.toPx()

                    // Left edge film sprocket holes
                    var y = 20f
                    while (y < size.height - 40f) {
                        drawRoundRect(
                            color = doodleColor,
                            topLeft = Offset(8f, y),
                            size = Size(perfWidth, perfHeight),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f, 2f)
                        )
                        y += spacing
                    }

                    // Top-right tiny star
                    val starPath = Path().apply {
                        val cx = size.width - 30f
                        val cy = 40f
                        moveTo(cx, cy - 8f)
                        lineTo(cx + 3f, cy - 2f)
                        lineTo(cx + 8f, cy)
                        lineTo(cx + 3f, cy + 2f)
                        lineTo(cx, cy + 8f)
                        lineTo(cx - 3f, cy + 2f)
                        lineTo(cx - 8f, cy)
                        lineTo(cx - 3f, cy - 2f)
                        close()
                    }
                    drawPath(starPath, doodleColor)
                }
            }

            "Botanical" -> {
                // Delicate organic corner flourishes
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val leafColor = if (isDark) Color(0x147A9684) else Color(0x184E775F)
                    drawCircle(
                        color = leafColor,
                        radius = 80.dp.toPx(),
                        center = Offset(size.width, 0f)
                    )
                    drawCircle(
                        color = leafColor.copy(alpha = 0.08f),
                        radius = 120.dp.toPx(),
                        center = Offset(0f, size.height)
                    )
                }
            }

            else -> {
                // "None" - Clean solid warm canvas
            }
        }

        // Main app content
        content()
    }
}
