package com.purnendu.hiremyidea.ui.insights

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import com.purnendu.hiremyidea.ui.theme.InsightsColors
import com.purnendu.hiremyidea.ui.theme.InsightsTypography

@Composable
fun StabilityChart(
    data: StabilityChartData,
    modifier: Modifier = Modifier
) {
    var containerSize by remember { mutableStateOf(IntSize.Zero) }
    val density = LocalDensity.current
    val bubbleWidth = 88.dp
    val bubbleHeight = 36.dp
    val leftPadding = 32.dp
    val rightPadding = 8.dp
    val topPadding = 10.dp
    val bottomPadding = 22.dp
    val topRange = 0.15f
    val bottomRange = 0.85f
    val safeMarkerIndex = data.markerIndex.coerceIn(0, (data.points.size - 1).coerceAtLeast(0))
    val pointCount = data.points.size.coerceAtLeast(2)
    val markerFraction = safeMarkerIndex / (pointCount - 1).toFloat()
    val bubbleOffsetX = with(density) {
        val chartLeftPx = leftPadding.toPx()
        val chartRightPx = rightPadding.toPx()
        val chartWidthPx = containerSize.width - chartLeftPx - chartRightPx
        val markerXPx = chartLeftPx + chartWidthPx * markerFraction
        markerXPx.toDp() - bubbleWidth / 2
    }
    val bubbleOffsetY = 4.dp

    // Auto-generate Y labels from actual data
    val dataMax = data.points.maxOrNull() ?: 0f
    val dataMin = data.points.minOrNull() ?: 0f
    val dataMid = (dataMax + dataMin) / 2f
    val autoYLabels = listOf(
        "${dataMax.toInt()}${data.yAxisUnit}",
        "${dataMid.toInt()}${data.yAxisUnit}",
        "${dataMin.toInt()}${data.yAxisUnit}"
    )

    // Calculate Y label padding synced with Canvas math
    val yLabelTopPadding = with(density) {
        val containerHeightPx = containerSize.height.toFloat()
        val topPaddingPx = topPadding.toPx()
        val bottomPaddingPx = bottomPadding.toPx()
        val chartHeightPx = containerHeightPx - topPaddingPx - bottomPaddingPx
        // Max value is drawn at chartTop + chartHeight * topRange
        (topPaddingPx + chartHeightPx * topRange).toDp()
    }
    val yLabelBottomPadding = with(density) {
        val containerHeightPx = containerSize.height.toFloat()
        val topPaddingPx = topPadding.toPx()
        val bottomPaddingPx = bottomPadding.toPx()
        val chartHeightPx = containerHeightPx - topPaddingPx - bottomPaddingPx
        // Min value is drawn at chartTop + chartHeight * bottomRange
        val minY = topPaddingPx + chartHeightPx * bottomRange
        (containerHeightPx - minY).toDp()
    }

    Box(
        modifier = modifier.onSizeChanged { containerSize = it }
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val chartLeft = leftPadding.toPx()
            val chartTop = topPadding.toPx()
            val chartRight = size.width - rightPadding.toPx()
            val chartBottom = size.height - bottomPadding.toPx()
            val chartWidth = chartRight - chartLeft
            val chartHeight = chartBottom - chartTop
            val normalized = normalizeValues(data.points)
            val points = normalized.mapIndexed { index, value ->
                val x = if (normalized.size == 1) {
                    chartLeft + chartWidth / 2f
                } else {
                    chartLeft + chartWidth * (index / (normalized.size - 1).toFloat())
                }
                val y = chartTop + chartHeight * (bottomRange - value * (bottomRange - topRange))
                Offset(x, y)
            }

            // --- Light purple filled wave (areaPath) ---
            val areaLinePath = Path().apply {
                moveTo(points.first().x, points.first().y)
                for (i in 1 until points.size) {
                    val prev = points[i - 1]
                    val current = points[i]
                    val midX = (prev.x + current.x) / 2
                    cubicTo(midX, prev.y, midX, current.y, current.x, current.y)
                }
            }
            val areaPath = Path().apply {
                addPath(areaLinePath)
                lineTo(points.last().x, chartBottom)
                lineTo(points.first().x, chartBottom)
                close()
            }
            drawPath(areaPath, InsightsColors.PurpleLight)

            // --- Dark purple overlay wave (overlayPath) ---
            val overlayLinePath = Path().apply {
                // Offset each point downward by a gradually increasing amount
                // to create the layered depth effect
                val overlayPoints = points.mapIndexed { index, point ->
                    val fraction = index / (points.size - 1).toFloat().coerceAtLeast(1f)
                    val yOffset = chartHeight * (0.06f + fraction * 0.10f)
                    Offset(point.x, (point.y + yOffset).coerceAtMost(chartBottom))
                }
                moveTo(overlayPoints.first().x, overlayPoints.first().y)
                for (i in 1 until overlayPoints.size) {
                    val prev = overlayPoints[i - 1]
                    val current = overlayPoints[i]
                    val midX = (prev.x + current.x) / 2
                    cubicTo(midX, prev.y, midX, current.y, current.x, current.y)
                }
            }
            val overlayPath = Path().apply {
                addPath(overlayLinePath)
                lineTo(points.last().x, chartBottom)
                lineTo(points.first().x, chartBottom)
                close()
            }
            drawPath(overlayPath, InsightsColors.Purple)

            val safeIndex = data.markerIndex.coerceIn(0, (points.size - 1).coerceAtLeast(0))
            val markerX = points.getOrNull(safeIndex)?.x ?: (chartLeft + chartWidth / 2f)
            val markerY = points.getOrNull(safeIndex)?.y ?: (chartTop + chartHeight / 2f)
            drawLine(
                color = InsightsColors.Muted.copy(alpha = 0.6f),
                start = Offset(markerX, chartTop + 8.dp.toPx()),
                end = Offset(markerX, chartBottom - 6.dp.toPx()),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f))
            )
            drawCircle(color = InsightsColors.Green, radius = 4.5.dp.toPx(), center = Offset(markerX, markerY))
        }

        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxHeight()
                .padding(top = yLabelTopPadding, bottom = yLabelBottomPadding),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            autoYLabels.forEach { label ->
                Text(label, style = InsightsTypography.Axis)
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = leftPadding, end = rightPadding)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val highlight = data.markerIndex.coerceIn(0, (data.xLabels.size - 1).coerceAtLeast(0))
            data.xLabels.forEachIndexed { index, label ->
                val style = if (index == highlight) InsightsTypography.AxisBold else InsightsTypography.Axis
                Text(label, style = style)
            }
        }

        Box(
            modifier = Modifier
                .offset(x = bubbleOffsetX, y = bubbleOffsetY)
                .size(width = bubbleWidth, height = bubbleHeight)
                .background(Color.Black, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = data.trendLabel,
                style = TextStyle(fontSize = 10.sp, color = Color.White, textAlign = TextAlign.Center)
            )
        }
    }
}

@Composable
fun WeightChart(
    data: WeightChartData,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val left = 28.dp.toPx()
            val right = 8.dp.toPx()
            val top = 8.dp.toPx()
            val bottom = 20.dp.toPx()
            val width = size.width - left - right
            val height = size.height - top - bottom
            val gridCount = data.yLabels.size.coerceAtLeast(3)
            val gridY = (0 until gridCount).map { index ->
                0.1f + (0.75f * index / (gridCount - 1).toFloat())
            }
            gridY.forEach { y ->
                drawLine(
                    color = InsightsColors.GreyLine,
                    start = Offset(left, top + height * y),
                    end = Offset(left + width, top + height * y),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f))
                )
            }
            val normalized = normalizeValues(data.points)
            val topRange = 0.15f
            val bottomRange = 0.85f
            val points = normalized.mapIndexed { index, value ->
                val x = if (normalized.size == 1) {
                    left + width / 2f
                } else {
                    left + width * (index / (normalized.size - 1).toFloat())
                }
                val y = top + height * (bottomRange - value * (bottomRange - topRange))
                Offset(x, y)
            }

            val linePath = Path().apply {
                moveTo(points.first().x, points.first().y)
                for (i in 1 until points.size) {
                    val prev = points[i - 1]
                    val current = points[i]
                    val midX = (prev.x + current.x) / 2
                    cubicTo(midX, prev.y, midX, current.y, current.x, current.y)
                }
            }

            val fillPath = Path().apply {
                addPath(linePath)
                lineTo(points.last().x, top + height)
                lineTo(points.first().x, top + height)
                close()
            }

            drawPath(
                fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(InsightsColors.Pink.copy(alpha = 0.35f), Color.Transparent),
                    startY = top,
                    endY = top + height
                )
            )
            drawPath(
                linePath,
                color = InsightsColors.Pink,
                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            points.drop(1).dropLast(1).forEach { point ->
                drawCircle(color = Color.White, radius = 4.dp.toPx(), center = point)
                drawCircle(color = InsightsColors.Pink, radius = 4.dp.toPx(), center = point, style = Stroke(width = 1.5.dp.toPx()))
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 4.dp, top = 6.dp, bottom = 22.dp)
                .height(90.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            data.yLabels.forEach { label ->
                Text(label, style = InsightsTypography.Axis)
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 28.dp, end = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            data.xLabels.forEach { label ->
                Text(label, style = InsightsTypography.Axis)
            }
        }
    }
}

@Composable
fun SymptomDonut(
    data: BodySignalsData,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        val donutSize = 180.dp
        Canvas(modifier = Modifier.size(donutSize)) {
            val stroke = Stroke(width = 24.dp.toPx(), cap = StrokeCap.Round)
            val total = data.segments.sumOf { it.value.toDouble() }.toFloat().coerceAtLeast(1f)
            val segments = data.segments
            var startAngle = -90f
            val gap = 4f
            segments.forEach { segment ->
                val sweep = segment.value / total * 360f - gap
                drawArc(
                    color = segment.color,
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = false,
                    style = stroke
                )
                startAngle += sweep + gap
            }
        }

        val bubbleOffsets = listOf(
            OffsetSpec((-90).dp, (-40).dp),
            OffsetSpec(90.dp, (-20).dp),
            OffsetSpec(60.dp, 80.dp),
            OffsetSpec((-85).dp, 70.dp)
        )
        data.segments.take(4).forEachIndexed { index, segment ->
            val offset = bubbleOffsets.getOrNull(index) ?: OffsetSpec(0.dp, 0.dp)
            SymptomBubble(
                text = "${segment.value.toInt()}%\\n${segment.label}",
                offsetX = offset.x,
                offsetY = offset.y
            )
        }
    }
}

@Composable
private fun SymptomBubble(text: String, offsetX: Dp, offsetY: Dp) {
    Box(
        modifier = Modifier
            .offset(x = offsetX, y = offsetY)
            .size(64.dp)
            .shadow(8.dp, CircleShape, ambientColor = InsightsColors.Shadow, spotColor = InsightsColors.Shadow)
            .background(Color.White, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, style = TextStyle(fontSize = 11.sp, textAlign = TextAlign.Center))
    }
}

@Composable
fun CycleBar(month: String, value: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value.toString(), style = InsightsTypography.AxisBold)
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .height(120.dp)
                .width(16.dp)
                .background(InsightsColors.Purple, RoundedCornerShape(8.dp))
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .height(32.dp)
                    .width(16.dp)
                    .background(InsightsColors.PurpleLight)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .height(28.dp)
                    .width(16.dp)
                    .background(InsightsColors.Green)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .height(34.dp)
                    .width(16.dp)
                    .background(InsightsColors.PinkLight)
            )

            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(10.dp)
                    .background(Color.White, CircleShape)
                    .border(1.dp, InsightsColors.Green, CircleShape)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = (-8).dp)
                    .size(10.dp)
                    .background(Color.White, CircleShape)
                    .border(1.dp, InsightsColors.Pink, CircleShape)
            )
        }
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(6.dp))
        Text(month, style = InsightsTypography.Axis)
    }
}

private data class OffsetSpec(val x: Dp, val y: Dp)

private fun normalizeValues(values: List<Float>): List<Float> {
    if (values.isEmpty()) return listOf(0.5f)
    val min = values.minOrNull() ?: 0f
    val max = values.maxOrNull() ?: 0f
    val range = (max - min).takeIf { it > 0f } ?: 1f
    return values.map { (it - min) / range }
}
