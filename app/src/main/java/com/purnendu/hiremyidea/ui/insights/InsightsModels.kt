package com.purnendu.hiremyidea.ui.insights

import androidx.compose.ui.graphics.Color
import com.purnendu.hiremyidea.ui.theme.InsightsColors
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

// High-level screen state

data class InsightsUiState(
    val stability: StabilitySummaryData,
    val cycleTrends: CycleTrendsData,
    val weight: WeightTrendsData,
    val signals: BodySignalsData,
    val lifestyle: LifestyleImpactData
)

// Stability

data class StabilitySummaryData(
    val summary: String,
    val scoreText: String,
    val chart: StabilityChartData
)

data class StabilityChartData(
    val yAxisUnit: String = "d",
    val xLabels: List<String>,
    val points: List<Float>,
    val markerIndex: Int,
    val trendLabel: String
)

// Cycle trends

data class CycleTrendsData(
    val months: List<String>,
    val values: List<Int>
)

// Weight trends

data class WeightTrendsData(
    val title: String,
    val subtitle: String,
    val isMonthlySelected: Boolean,
    val chart: WeightChartData
)

data class WeightChartData(
    val yLabels: List<String>,
    val xLabels: List<String>,
    val points: List<Float>
)

// Body signals

data class BodySignalsData(
    val title: String,
    val subtitle: String,
    val segments: List<Segment>
)

// Lifestyle impact

data class LifestyleImpactData(
    val title: String,
    val periodLabel: String,
    val rows: List<HeatRowData>
)

data class HeatRowData(
    val label: String,
    val baseColor: Color,
    val values: List<Float>
)

data class Segment(val label: String, val value: Float, val color: Color)

// Default sample data

object InsightsSampleData {
    // Generate 4 months: 2 before current, current, 1 after
    private val currentDate = LocalDate.now()
    private val recentMonths = (-2..1).map { offset ->
        currentDate.plusMonths(offset.toLong())
            .month
            .getDisplayName(TextStyle.SHORT, Locale.getDefault())
    }

    val state = InsightsUiState(
        stability = StabilitySummaryData(
            summary = "Based on your recent logs and symptom\npatterns.",
            scoreText = "78%",
            chart = StabilityChartData(
                yAxisUnit = "d",
                xLabels = recentMonths,
                points = listOf(24f, 25f, 26.5f, 28.5f),
                markerIndex = 2, // Current month is at index 2 (3rd position)
                trendLabel = "Stability\nImproving"
            )
        ),
        cycleTrends = CycleTrendsData(
            months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun"),
            values = listOf(28, 30, 28, 32, 28, 28)
        ),
        weight = WeightTrendsData(
            title = "Your weight",
            subtitle = "in kg",
            isMonthlySelected = true,
            chart = WeightChartData(
                yLabels = listOf("75", "50", "25"),
                xLabels = listOf("Jan", "Feb", "Mar", "Apr", "May"),
                points = listOf(35f, 48f, 40f, 72f, 55f, 52f)
            )
        ),
        signals = BodySignalsData(
            title = "Symptom Trends",
            subtitle = "Compared to last cycle",
            segments = listOf(
                Segment("Mood", 30f, InsightsColors.PinkLight),
                Segment("Bloating", 31f, InsightsColors.Purple),
                Segment("Fatigue", 21f, InsightsColors.Pink),
                Segment("Acne", 17f, InsightsColors.Green)
            )
        ),
        lifestyle = LifestyleImpactData(
            title = "Correlation Strength",
            periodLabel = "4 months",
            rows = listOf(
                HeatRowData(
                    label = "Sleep",
                    baseColor = InsightsColors.Purple,
                    values = listOf(1f, 0.9f, 0.7f, 0.6f, 0.6f, 0.6f, 0.6f, 0f, 0f, 0f)
                ),
                HeatRowData(
                    label = "Hydrate",
                    baseColor = InsightsColors.Pink,
                    values = listOf(1f, 0.7f, 0.6f, 0.5f, 0f, 0f, 0f, 0f, 0f, 0f)
                ),
                HeatRowData(
                    label = "Caffeine",
                    baseColor = InsightsColors.Green,
                    values = listOf(1f, 0.9f, 0.7f, 0.5f, 0f, 0f, 0f, 0f, 0f, 0f)
                ),
                HeatRowData(
                    label = "Exercise",
                    baseColor = InsightsColors.PinkLight,
                    values = listOf(0.8f, 0.7f, 0.6f, 0.6f, 0f, 0f, 0f, 0f, 0f, 0f)
                )
            )
        )
    )
}
