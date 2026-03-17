package com.purnendu.hiremyidea.ui.insights

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.purnendu.hiremyidea.ui.theme.InsightsColors
import com.purnendu.hiremyidea.ui.theme.InsightsTypography

@Composable
fun TitleRow() {
    Box(modifier = Modifier.fillMaxWidth().height(32.dp)) {
        FourDotIcon(modifier = Modifier.align(Alignment.CenterStart))
        Text(
            text = "Insights",
            modifier = Modifier.align(Alignment.Center),
            style = InsightsTypography.Title,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun FourDotIcon(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Dot(InsightsColors.DotDark)
            Dot(InsightsColors.DotLight)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Dot(InsightsColors.DotLight)
            Dot(InsightsColors.DotDark)
        }
    }
}

@Composable
private fun Dot(color: Color) {
    Box(modifier = Modifier.size(8.dp).background(color, CircleShape))
}
