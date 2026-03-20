package com.purnendu.hiremyidea

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.util.Calendar
import kotlin.math.cos
import kotlin.math.sin
@Composable
fun AnalogClock(
    modifier: Modifier = Modifier,
    clockColor: Color = Color.Black,
    hourHandColor: Color = Color.Black,
    minuteHandColor: Color = Color.DarkGray,
    secondHandColor: Color = Color.Red,
    backgroundColor: Color = Color.White
) {
    var currentTimeMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            currentTimeMillis = System.currentTimeMillis()
            delay(1000)
        }
    }
    val calendar = remember(currentTimeMillis) {
        Calendar.getInstance().apply { timeInMillis = currentTimeMillis }
    }
    val hours = calendar.get(Calendar.HOUR)
    val minutes = calendar.get(Calendar.MINUTE)
    val seconds = calendar.get(Calendar.SECOND)
    val milliseconds = calendar.get(Calendar.MILLISECOND)
    val preciseSeconds = seconds + milliseconds / 1000f
    val preciseMinutes = minutes + preciseSeconds / 60f
    val preciseHours = (hours % 12) + preciseMinutes / 60f
    Canvas(modifier = modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = minOf(centerX, centerY) * 0.85f
        drawCircle(
            color = backgroundColor,
            radius = radius,
            center = Offset(centerX, centerY)
        )
        drawCircle(
            color = clockColor,
            radius = radius,
            center = Offset(centerX, centerY),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 8.dp.toPx())
        )
        for (i in 0 until 60) {
            val angle = Math.toRadians((i * 6 - 90).toDouble())
            val isHourMark = i % 5 == 0
            val startRadius = radius - (if (isHourMark) 30.dp.toPx() else 15.dp.toPx())
            val endRadius = radius - 10.dp.toPx()
            drawLine(
                color = clockColor,
                start = Offset(
                    centerX + startRadius * cos(angle).toFloat(),
                    centerY + startRadius * sin(angle).toFloat()
                ),
                end = Offset(
                    centerX + endRadius * cos(angle).toFloat(),
                    centerY + endRadius * sin(angle).toFloat()
                ),
                strokeWidth = if (isHourMark) 4.dp.toPx() else 2.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
        drawHourHand(centerX, centerY, radius, preciseHours, hourHandColor)
        drawMinuteHand(centerX, centerY, radius, preciseMinutes, minuteHandColor)
        drawSecondHand(centerX, centerY, radius, preciseSeconds, secondHandColor)
        drawCircle(
            color = clockColor,
            radius = 12.dp.toPx(),
            center = Offset(centerX, centerY)
        )
        drawCircle(
            color = backgroundColor,
            radius = 6.dp.toPx(),
            center = Offset(centerX, centerY)
        )
    }
}
private fun DrawScope.drawHourHand(
    centerX: Float,
    centerY: Float,
    radius: Float,
    hours: Float,
    color: Color
) {
    val angle = Math.toRadians(((hours * 30) - 90).toDouble())
    val handLength = radius * 0.5f
    rotate(degrees = (hours * 30).toFloat(), pivot = Offset(centerX, centerY)) {
        drawLine(
            color = color,
            start = Offset(centerX, centerY),
            end = Offset(centerX, centerY - handLength),
            strokeWidth = 10.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}
private fun DrawScope.drawMinuteHand(
    centerX: Float,
    centerY: Float,
    radius: Float,
    minutes: Float,
    color: Color
) {
    val handLength = radius * 0.7f
    rotate(degrees = minutes * 6f, pivot = Offset(centerX, centerY)) {
        drawLine(
            color = color,
            start = Offset(centerX, centerY),
            end = Offset(centerX, centerY - handLength),
            strokeWidth = 6.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}
private fun DrawScope.drawSecondHand(
    centerX: Float,
    centerY: Float,
    radius: Float,
    seconds: Float,
    color: Color
) {
    val handLength = radius * 0.8f
    rotate(degrees = seconds * 6f, pivot = Offset(centerX, centerY)) {
        drawLine(
            color = color,
            start = Offset(centerX, centerY + 30.dp.toPx()),
            end = Offset(centerX, centerY - handLength),
            strokeWidth = 3.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}