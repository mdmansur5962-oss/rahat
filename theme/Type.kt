package com.aiagent.assistant.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Custom font design screenshot পাওয়ার পর যোগ হবে। এখন system font চলছে (বাংলা সহ)।
// বাংলা অক্ষর লম্বা, তাই lineHeight ইচ্ছে করে একটু বেশি রাখা হয়েছে।
private val AppFont = FontFamily.Default

private fun style(weight: FontWeight, size: Int, line: Int, letter: Double = 0.0) = TextStyle(
    fontFamily = AppFont,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = line.sp,
    letterSpacing = letter.sp
)

val AgentTypography = Typography(
    displayLarge = style(FontWeight.Bold, 52, 68, -0.5),
    displayMedium = style(FontWeight.Bold, 42, 56, -0.25),
    displaySmall = style(FontWeight.Bold, 34, 46),
    headlineLarge = style(FontWeight.Bold, 30, 40),
    headlineMedium = style(FontWeight.Bold, 26, 36),
    headlineSmall = style(FontWeight.SemiBold, 22, 32),
    titleLarge = style(FontWeight.SemiBold, 20, 30),
    titleMedium = style(FontWeight.SemiBold, 16, 26, 0.1),
    titleSmall = style(FontWeight.Medium, 14, 22, 0.1),
    bodyLarge = style(FontWeight.Normal, 16, 26, 0.3),
    bodyMedium = style(FontWeight.Normal, 14, 23, 0.2),
    bodySmall = style(FontWeight.Normal, 12, 19, 0.3),
    labelLarge = style(FontWeight.Medium, 14, 22, 0.1),
    labelMedium = style(FontWeight.Medium, 12, 18, 0.3),
    labelSmall = style(FontWeight.Medium, 11, 16, 0.4)
)
