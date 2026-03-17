package com.purnendu.hiremyidea.ui.insights

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.purnendu.hiremyidea.ui.theme.InsightsColors

@Composable
fun BottomNavBar(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .shadow(10.dp, RoundedCornerShape(32.dp), ambientColor = InsightsColors.Shadow, spotColor = InsightsColors.Shadow)
                .background(Color.White, RoundedCornerShape(32.dp))
                .padding(horizontal = 28.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(label = "Home", active = false)
            NavItem(label = "Track", active = false)
            NavItem(label = "Insights", active = true)
        }

        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = 8.dp)
                .size(56.dp)
                .shadow(8.dp, CircleShape, ambientColor = InsightsColors.Shadow, spotColor = InsightsColors.Shadow)
                .background(Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(18.dp)) {
                val strokeWidth = 2.2.dp.toPx()
                drawLine(
                    color = Color.Black,
                    start = Offset(size.width / 2, 0f),
                    end = Offset(size.width / 2, size.height),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = Color.Black,
                    start = Offset(0f, size.height / 2),
                    end = Offset(size.width, size.height / 2),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

@Composable
private fun NavItem(label: String, active: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Canvas(modifier = Modifier.size(22.dp)) {
            val stroke = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
            if (label == "Home") {
                val roofPath = Path().apply {
                    moveTo(size.width * 0.1f, size.height * 0.55f)
                    lineTo(size.width * 0.5f, size.height * 0.2f)
                    lineTo(size.width * 0.9f, size.height * 0.55f)
                }
                drawPath(roofPath, color = if (active) Color.Black else InsightsColors.Muted, style = stroke)
                drawRoundRect(
                    color = if (active) Color.Black else InsightsColors.Muted,
                    topLeft = Offset(size.width * 0.2f, size.height * 0.55f),
                    size = Size(size.width * 0.6f, size.height * 0.35f),
                    cornerRadius = CornerRadius(4f, 4f),
                    style = Stroke(width = 2.dp.toPx())
                )
            } else if (label == "Track") {
                drawCircle(
                    color = if (active) Color.Black else InsightsColors.Muted,
                    radius = size.minDimension * 0.4f,
                    style = Stroke(width = 2.dp.toPx())
                )
                drawLine(
                    color = if (active) Color.Black else InsightsColors.Muted,
                    start = Offset(size.width / 2, size.height / 2),
                    end = Offset(size.width / 2, size.height * 0.28f),
                    strokeWidth = 2.dp.toPx()
                )
            } else {
                val barWidth = size.width * 0.14f
                val gap = size.width * 0.12f
                val base = size.height * 0.75f
                listOf(0f, barWidth + gap, (barWidth + gap) * 2).forEachIndexed { index, x ->
                    val height = size.height * (0.35f + index * 0.18f)
                    drawRoundRect(
                        color = if (active) Color.Black else InsightsColors.Muted,
                        topLeft = Offset(x, base - height),
                        size = Size(barWidth, height),
                        cornerRadius = CornerRadius(3f, 3f)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = TextStyle(
                fontSize = 11.sp,
                fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal,
                color = if (active) Color.Black else InsightsColors.Muted
            )
        )
    }
}
