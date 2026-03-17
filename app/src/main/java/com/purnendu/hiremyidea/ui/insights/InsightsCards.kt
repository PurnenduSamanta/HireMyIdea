package com.purnendu.hiremyidea.ui.insights

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.purnendu.hiremyidea.ui.theme.InsightsColors
import com.purnendu.hiremyidea.ui.theme.InsightsTypography

@Composable
fun StabilityCard(data: StabilitySummaryData) {
    CardContainer {
        Text(
            text = data.summary,
            style = InsightsTypography.BodyMuted
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "Stability Score", style = InsightsTypography.Body)
        Text(text = data.scoreText, style = InsightsTypography.Score)
        Spacer(modifier = Modifier.height(12.dp))
        StabilityChart(data = data.chart, modifier = Modifier.fillMaxWidth().height(150.dp))
    }
}

@Composable
fun CycleTrendsCard(data: CycleTrendsData) {
    CardContainer {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircleArrow("‹")
            Spacer(modifier = Modifier.width(6.dp))
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                val count = minOf(data.months.size, data.values.size)
                for (index in 0 until count) {
                    CycleBar(data.months[index], data.values[index])
                }
            }
            Spacer(modifier = Modifier.width(6.dp))
            CircleArrow("›")
        }
    }
}

@Composable
fun BodyMetabolicCard(data: WeightTrendsData) {
    CardContainer {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = data.title, style = InsightsTypography.Body)
                Text(text = data.subtitle, style = InsightsTypography.BodyMuted)
            }
            TogglePill(text = "Monthly", selected = data.isMonthlySelected)
            Spacer(modifier = Modifier.width(6.dp))
            TogglePill(text = "Weekly", selected = !data.isMonthlySelected)
        }
        Spacer(modifier = Modifier.height(14.dp))
        WeightChart(data = data.chart, modifier = Modifier.fillMaxWidth().height(150.dp))
    }
}

@Composable
fun BodySignalsCard(data: BodySignalsData) {
    CardContainer {
        Text(text = data.title, style = InsightsTypography.Body)
        Text(text = data.subtitle, style = InsightsTypography.BodyMuted)
        Spacer(modifier = Modifier.height(16.dp))
        SymptomDonut(data = data, modifier = Modifier.fillMaxWidth().height(240.dp))
    }
}

@Composable
fun LifestyleImpactCard(data: LifestyleImpactData) {
    CardContainer {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(text = data.title, style = InsightsTypography.Body)
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .height(26.dp)
                    .background(InsightsColors.Chip, RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = data.periodLabel, style = InsightsTypography.Axis)
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        data.rows.forEach { row ->
            HeatRow(row)
        }
    }
}
