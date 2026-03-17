package com.purnendu.hiremyidea.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)

// Insights screen typography
object InsightsTypography {
    val Title = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
    val Section = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
    val Body = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.Black)
    val BodyMuted = TextStyle(fontSize = 12.sp, color = InsightsColors.Muted)
    val Score = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
    val Axis = TextStyle(fontSize = 10.sp, color = InsightsColors.Muted)
    val AxisBold = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
    val StatusBar = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
}
