package com.purnendu.hiremyidea.ui.insights

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope.Companion.DefaultBlendMode
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.purnendu.hiremyidea.ui.theme.HireMyIdeaTheme
import com.purnendu.hiremyidea.ui.theme.InsightsColors

@Composable
fun InsightsScreen(state: InsightsUiState = InsightsSampleData.state) {
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize().background(InsightsColors.Background)) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val washHeight = 260.dp.toPx().coerceAtMost(size.height)
            val fadeStart = (washHeight * 0.55f).coerceAtMost(size.height)
            val fadeEnd = (washHeight * 1.45f).coerceAtMost(size.height)
            val fadeStartStop = (fadeStart / size.height).coerceIn(0f, 1f)
            val fadeEndStop = (fadeEnd / size.height).coerceIn(0f, 1f)

            // Horizontal tint (top-left whitish to right pinkish)
            drawRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.18f),
                        InsightsColors.Pink.copy(alpha = 0.14f),
                        InsightsColors.Pink.copy(alpha = 0.08f)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f)
                ),
                size = Size(width = size.width, height = fadeEnd)
            )

            // Long vertical fade to avoid a visible boundary near washHeight
            drawRect(
                brush = Brush.verticalGradient(
                    colorStops = arrayOf(
                        0f to Color.Transparent,
                        fadeStartStop to Color.Transparent,
                        fadeEndStop to InsightsColors.Background
                    )
                )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .statusBarsPadding()
                .padding(horizontal = 16.dp)
                .padding(bottom = 130.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            TitleRow()
            Spacer(modifier = Modifier.height(18.dp))

            SectionTitle("Stability Summary")
            Spacer(modifier = Modifier.height(12.dp))
            StabilityCard(state.stability)

            Spacer(modifier = Modifier.height(20.dp))
            SectionTitle("Cycle Trends")
            Spacer(modifier = Modifier.height(12.dp))
            CycleTrendsCard(state.cycleTrends)

            Spacer(modifier = Modifier.height(20.dp))
            SectionTitle("Body & Metabolic Trends")
            Spacer(modifier = Modifier.height(12.dp))
            BodyMetabolicCard(state.weight)

            Spacer(modifier = Modifier.height(20.dp))
            SectionTitle("Body Signals")
            Spacer(modifier = Modifier.height(12.dp))
            BodySignalsCard(state.signals)

            Spacer(modifier = Modifier.height(20.dp))
            SectionTitle("Lifestyle Impact")
            Spacer(modifier = Modifier.height(12.dp))
            LifestyleImpactCard(state.lifestyle)

            Spacer(modifier = Modifier.height(24.dp))
        }

        BottomNavBar(modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Preview(showBackground = true)
@Composable
private fun InsightsScreenPreview() {
    HireMyIdeaTheme {
        Surface(color = InsightsColors.Background) {
            InsightsScreen()
        }
    }
}
