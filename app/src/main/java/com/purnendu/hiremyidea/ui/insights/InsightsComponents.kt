package com.purnendu.hiremyidea.ui.insights

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.purnendu.hiremyidea.ui.theme.InsightsColors
import com.purnendu.hiremyidea.ui.theme.InsightsTypography

@Composable
fun SectionTitle(text: String) {
    Text(text = text, style = InsightsTypography.Section)
}

@Composable
fun CardContainer(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(10.dp, RoundedCornerShape(16.dp), ambientColor = InsightsColors.Shadow, spotColor = InsightsColors.Shadow)
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(16.dp),
        content = content
    )
}

@Composable
fun TogglePill(text: String, selected: Boolean) {
    Box(
        modifier = Modifier
            .height(28.dp)
            .background(if (selected) Color.Black else InsightsColors.Chip, RoundedCornerShape(14.dp))
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = if (selected) Color.White else InsightsColors.Muted
            )
        )
    }
}

@Composable
fun CircleArrow(symbol: String) {
    Box(
        modifier = Modifier
            .size(22.dp)
            .border(1.dp, InsightsColors.Purple, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(symbol, style = TextStyle(fontSize = 12.sp, color = InsightsColors.Purple))
    }
}

@Composable
fun HeatRow(data: HeatRowData) {
    val colors = data.values.map { intensity ->
        if (intensity <= 0f) {
            InsightsColors.GreyLine
        } else {
            val clamped = intensity.coerceIn(0f, 1f)
            data.baseColor.copy(alpha = 0.25f + 0.75f * clamped)
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = data.label, style = InsightsTypography.Axis, modifier = Modifier.width(64.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            colors.forEach { color ->
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .background(color, RoundedCornerShape(4.dp))
                )
            }
        }
    }
}
