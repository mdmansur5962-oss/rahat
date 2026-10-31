package com.aiagent.assistant.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Material ColorScheme-এ নেই এমন বাড়তি রঙ ও gradient */
@Immutable
data class AgentExtendedColors(
    val success: Color,
    val warning: Color,
    val glassFill: Color,
    val glassHighlight: Color,
    val glassBorder: Color,
    val backgroundTint: Color,
    val brand: Brush,
    val danger: Brush,
    val isDark: Boolean
)

@Immutable
data class Spacing(
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 12.dp,
    val lg: Dp = 16.dp,
    val xl: Dp = 24.dp,
    val xxl: Dp = 32.dp,
    val huge: Dp = 48.dp
)

val LocalAgentColors = staticCompositionLocalOf<AgentExtendedColors> {
    error("AgentExtendedColors not provided. Wrap your UI in AgentTheme { }.")
}

val LocalSpacing = staticCompositionLocalOf { Spacing() }

/** ব্যবহার: AgentDesign.colors.success, AgentDesign.spacing.lg */
object AgentDesign {
    val colors: AgentExtendedColors
        @Composable
        @ReadOnlyComposable
        get() = LocalAgentColors.current

    val spacing: Spacing
        @Composable
        @ReadOnlyComposable
        get() = LocalSpacing.current
}
