package com.purnendu.hiremyidea.ui.insights

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
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
import kotlin.math.pow
import kotlin.math.sqrt

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
    val bottomRange = 1.0f
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
            val range = (dataMax - dataMin).takeIf { it > 0f } ?: 1f
            val points = data.points.mapIndexed { index, rawValue ->
                val value = (rawValue - dataMin) / range
                val x = if (data.points.size == 1) {
                    chartLeft + chartWidth / 2f
                } else {
                    chartLeft + chartWidth * (index / (data.points.size - 1).toFloat())
                }
                val y = chartTop + chartHeight * (bottomRange - value * (bottomRange - topRange))
                Offset(x, y)
            }

            // Draw visible axis lines representing the L-shaped origin graph as requested
            drawLine(
                color = InsightsColors.Muted.copy(alpha = 0.5f),
                start = Offset(chartLeft, chartTop),
                end = Offset(chartLeft, chartBottom),
                strokeWidth = 1.dp.toPx()
            )
            drawLine(
                color = InsightsColors.Muted.copy(alpha = 0.5f),
                start = Offset(chartLeft, chartBottom),
                end = Offset(chartRight, chartBottom),
                strokeWidth = 1.dp.toPx()
            )

            // --- Light purple filled wave (areaPath) ---
            val areaLinePath = Path().apply {
                moveTo(points.first().x, points.first().y)
                val tension = 0.20f
                for (i in 1 until points.size) {
                    val p0 = points[(i - 2).coerceAtLeast(0)]
                    val p1 = points[i - 1]
                    val p2 = points[i]
                    val p3 = points[(i + 1).coerceAtMost(points.size - 1)]

                    val cp1x = p1.x + (p2.x - p0.x) * tension
                    val cp1y = p1.y + (p2.y - p0.y) * tension
                    val cp2x = p2.x - (p3.x - p1.x) * tension
                    val cp2y = p2.y - (p3.y - p1.y) * tension

                    cubicTo(cp1x, cp1y, cp2x, cp2y, p2.x, p2.y)
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
                    val yOffset = chartHeight * (0.06f + fraction * 0.1f)
                    Offset(point.x, (point.y + yOffset).coerceAtMost(chartBottom))
                }
                moveTo(overlayPoints.first().x, overlayPoints.first().y)
                val tension = 0.20f
                for (i in 1 until overlayPoints.size) {
                    val p0 = overlayPoints[(i - 2).coerceAtLeast(0)]
                    val p1 = overlayPoints[i - 1]
                    val p2 = overlayPoints[i]
                    val p3 = overlayPoints[(i + 1).coerceAtMost(overlayPoints.size - 1)]

                    val cp1x = p1.x + (p2.x - p0.x) * tension
                    val cp1y = p1.y + (p2.y - p0.y) * tension
                    val cp2x = p2.x - (p3.x - p1.x) * tension
                    val cp2y = p2.y - (p3.y - p1.y) * tension

                    cubicTo(cp1x, cp1y, cp2x, cp2y, p2.x, p2.y)
                }
            }
            val overlayPath = Path().apply {
                addPath(overlayLinePath)
                lineTo(points.last().x, chartBottom-(chartHeight*0.30f))
                lineTo(points.first().x, chartBottom)
                close()
            }
            drawPath(overlayPath, InsightsColors.Purple)

            val safeIndex = data.markerIndex.coerceIn(0, (points.size - 1).coerceAtLeast(0))
            val markerX = points.getOrNull(safeIndex)?.x ?: (chartLeft + chartWidth / 2f)
            val markerY = points.getOrNull(safeIndex)?.y ?: (chartTop + chartHeight / 2f)

            val distanceBetweenMiddlePortionOfBubbleToDataPoint= sqrt((markerX - (bubbleOffsetX.toPx()+(bubbleWidth.toPx()/2))).pow(2) + (markerY - (bubbleOffsetY.toPx()+bubbleHeight.toPx())).pow(2))
            val markerCircleOffsetY = markerY-(distanceBetweenMiddlePortionOfBubbleToDataPoint/2)
            drawCircle(color = InsightsColors.Green, radius = 4.5.dp.toPx(), center = Offset(markerX,markerCircleOffsetY ))

            drawLine(
                color = InsightsColors.Muted.copy(alpha = 0.6f),
                strokeWidth = 5f,
                start = Offset(markerX, markerCircleOffsetY+10.dp.toPx()),
                end = Offset(markerX, chartBottom),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
            )
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

        val sizeOfTipPortionOfBubble = 8.dp
        Box(
            modifier = Modifier
                .offset(x = bubbleOffsetX, y = bubbleOffsetY)
                .size(width = bubbleWidth, height = bubbleHeight)
                .drawBehind{
                    val tipSize = sizeOfTipPortionOfBubble.toPx()
                    val path = Path().apply {
                        moveTo(size.width / 2 - tipSize, size.height)
                        lineTo(size.width / 2, size.height + tipSize)
                        lineTo(size.width / 2 + tipSize, size.height)
                        close()
                    }
                    drawPath(path, color = Color.Black)
                }
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
    var containerSize by remember { mutableStateOf(IntSize.Zero) }
    val leftPadding = 28.dp
    val rightPadding = 8.dp
    val topPadding = 8.dp
    val bottomPadding = 20.dp
    val topRange = 0.15f
    val bottomRange = 0.85f

    val density = LocalDensity.current

    // Auto-generate Y labels from actual data (same approach as StabilityChart)
    val dataMax = data.points.maxOrNull() ?: 0f
    val dataMin = data.points.minOrNull() ?: 0f
    val dataMid = (dataMax + dataMin) / 2f
    val autoYLabels = listOf(
        "${dataMax.toInt()}",
        "${dataMid.toInt()}",
        "${dataMin.toInt()}"
    )

    val yLabelFractions = listOf(topRange, (topRange + bottomRange) / 2f, bottomRange)

    // Approximate text line height for Y labels to center them on grid lines
    val yLabelLineHeight = with(density) { 10.sp.toDp() }

    Box(modifier = modifier.onSizeChanged { containerSize = it }) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val chartLeft = leftPadding.toPx()
            val chartWidth = size.width - leftPadding.toPx() - rightPadding.toPx()
            val chartHeight = size.height - topPadding.toPx() - bottomPadding.toPx()

            // Draw dashed horizontal grid lines at each Y label position
            yLabelFractions.forEach { fraction ->
                val y = topPadding.toPx() + chartHeight * fraction
                drawLine(
                    color = InsightsColors.GreyLine,
                    strokeWidth = 1.dp.toPx(),
                    start = Offset(chartLeft, y),
                    end = Offset(chartLeft + chartWidth, y),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f))
                )
            }

            val normalized = normalizeValues(data.points)
            val points = normalized.mapIndexed { index, value ->
                val x = if (normalized.size == 1) {
                    chartLeft + chartWidth / 2f
                } else {
                    chartLeft + chartWidth * (index / (normalized.size - 1).toFloat())
                }
                val y = topPadding.toPx() + chartHeight * (bottomRange - value * (bottomRange - topRange))
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
                lineTo(points.last().x, topPadding.toPx() + chartHeight)
                lineTo(points.first().x, topPadding.toPx() + chartHeight)
                close()
            }

            drawPath(
                fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(InsightsColors.Pink.copy(alpha = 0.35f), Color.Transparent),
                    startY = topPadding.toPx(),
                    endY = topPadding.toPx() + chartHeight
                )
            )
            drawPath(
                linePath,
                color = InsightsColors.Pink,
                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            points.forEach { point ->
                drawCircle(color = Color.White, radius = 4.dp.toPx(), center = point)
                drawCircle(color = InsightsColors.Pink, radius = 4.dp.toPx(), center = point, style = Stroke(width = 1.5.dp.toPx()))
            }
        }

        // Y labels — each positioned so its vertical center aligns with the dashed grid line
        Box(modifier = Modifier.matchParentSize()) {
            autoYLabels.forEachIndexed { index, label ->
                val fraction = yLabelFractions[index]
                val offsetY = with(density) {
                    val chartHeightPx = containerSize.height - topPadding.toPx() - bottomPadding.toPx()
                    val yPx = topPadding.toPx() + chartHeightPx * fraction
                    yPx.toDp() - yLabelLineHeight / 2
                }
                Text(
                    text = label,
                    style = InsightsTypography.Axis,
                    modifier = Modifier.offset(y = offsetY)
                )
            }
        }

        // X labels — each positioned directly below its data point
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
        ) {
            val pointCount = data.points.size.coerceAtLeast(2)
            data.xLabels.forEachIndexed { index, label ->
                val fraction = index / (pointCount - 1).toFloat()
                val offsetX = with(density) {
                    val chartLeftPx = leftPadding.toPx()
                    val chartRightPx = rightPadding.toPx()
                    val chartWidthPx = containerSize.width - chartLeftPx - chartRightPx
                    val xPx = chartLeftPx + chartWidthPx * fraction
                    xPx.toDp()
                }
                Text(
                    text = label,
                    style = InsightsTypography.Axis,
                    modifier = Modifier.offset(x = offsetX),
                    textAlign = TextAlign.Center
                )
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
        Spacer(modifier = Modifier.height(4.dp))
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
        Spacer(modifier = Modifier.height(6.dp))
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
