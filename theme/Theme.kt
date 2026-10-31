package com.aiagent.assistant.presentation.theme

import android.app.Activity
import android.content.ContextWrapper
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Density
import androidx.core.view.WindowCompat

enum class ThemeMode { SYSTEM, LIGHT, DARK }

private fun onColorFor(color: Color): Color =
    if (color.luminance() > 0.55f) Color(0xFF101018) else Color.White

private fun darkScheme(a: AccentPalette): ColorScheme {
    val bg = DarkBackground
    return darkColorScheme(
        primary = a.primary,
        onPrimary = onColorFor(a.primary),
        primaryContainer = lerp(bg, a.primary, 0.30f),
        onPrimaryContainer = lerp(a.primary, Color.White, 0.80f),
        secondary = a.secondary,
        onSecondary = onColorFor(a.secondary),
        secondaryContainer = lerp(bg, a.secondary, 0.25f),
        onSecondaryContainer = lerp(a.secondary, Color.White, 0.80f),
        tertiary = a.tertiary,
        onTertiary = onColorFor(a.tertiary),
        tertiaryContainer = lerp(bg, a.tertiary, 0.28f),
        onTertiaryContainer = lerp(a.tertiary, Color.White, 0.80f),
        background = bg,
        onBackground = DarkOnSurface,
        surface = DarkSurface,
        onSurface = DarkOnSurface,
        surfaceVariant = DarkSurfaceVariant,
        onSurfaceVariant = DarkOnSurfaceVariant,
        surfaceTint = a.primary,
        inverseSurface = DarkOnSurface,
        inverseOnSurface = LightOnSurface,
        inversePrimary = lerp(a.primary, Color.Black, 0.20f),
        outline = DarkOutline,
        outlineVariant = Color(0xFF22346A),
        error = ErrorDark,
        onError = Color.White,
        errorContainer = Color(0xFF5C1226),
        onErrorContainer = Color(0xFFFFD9DF),
        scrim = Color.Black,
        surfaceContainerLowest = Color(0xFF040817),
        surfaceContainerLow = Color(0xFF08102A),
        surfaceContainer = Color(0xFF0C1736),
        surfaceContainerHigh = Color(0xFF122046),
        surfaceContainerHighest = Color(0xFF192A57)
    )
}

private fun lightScheme(a: AccentPalette): ColorScheme {
    val p = lerp(a.primary, Color.Black, 0.10f)
    val s = lerp(a.secondary, Color.Black, 0.25f)
    val t = lerp(a.tertiary, Color.Black, 0.12f)
    return lightColorScheme(
        primary = p,
        onPrimary = Color.White,
        primaryContainer = lerp(Color.White, a.primary, 0.20f),
        onPrimaryContainer = lerp(a.primary, Color.Black, 0.65f),
        secondary = s,
        onSecondary = Color.White,
        secondaryContainer = lerp(Color.White, a.secondary, 0.22f),
        onSecondaryContainer = lerp(a.secondary, Color.Black, 0.70f),
        tertiary = t,
        onTertiary = Color.White,
        tertiaryContainer = lerp(Color.White, a.tertiary, 0.22f),
        onTertiaryContainer = lerp(a.tertiary, Color.Black, 0.65f),
        background = LightBackground,
        onBackground = LightOnSurface,
        surface = LightSurface,
        onSurface = LightOnSurface,
        surfaceVariant = LightSurfaceVariant,
        onSurfaceVariant = LightOnSurfaceVariant,
        surfaceTint = p,
        inverseSurface = LightOnSurface,
        inverseOnSurface = Color(0xFFF1EFFF),
        inversePrimary = a.primary,
        outline = LightOutline,
        outlineVariant = Color(0xFFD3CFE6),
        error = ErrorLight,
        onError = Color.White,
        errorContainer = Color(0xFFFFD9DF),
        onErrorContainer = Color(0xFF5C1226),
        scrim = Color.Black,
        surfaceContainerLowest = Color.White,
        surfaceContainerLow = Color(0xFFF8F7FF),
        surfaceContainer = Color(0xFFF1EFFC),
        surfaceContainerHigh = Color(0xFFEBE8F8),
        surfaceContainerHighest = Color(0xFFE4E1F3)
    )
}

private fun extendedColors(dark: Boolean, a: AccentPalette, scheme: ColorScheme): AgentExtendedColors =
    if (dark) {
        AgentExtendedColors(
            success = Color(0xFF19D3A2),
            warning = Color(0xFFFBBF24),
            glassFill = Color(0xFF6C8BFF).copy(alpha = 0.07f),
            glassHighlight = Color(0xFF9DB4FF).copy(alpha = 0.12f),
            glassBorder = Color(0xFF5B7FE0).copy(alpha = 0.28f),
            backgroundTint = lerp(DarkBackground, a.primary, 0.20f),
            brand = Brush.linearGradient(listOf(a.primary, a.secondary)),
            danger = Brush.linearGradient(listOf(Color(0xFFFF5470), Color(0xFFFF8A5C))),
            isDark = true
        )
    } else {
        AgentExtendedColors(
            success = Color(0xFF0F9D6B),
            warning = Color(0xFFB7791F),
            glassFill = Color.White.copy(alpha = 0.72f),
            glassHighlight = Color.White.copy(alpha = 0.95f),
            glassBorder = scheme.primary.copy(alpha = 0.16f),
            backgroundTint = lerp(LightBackground, a.primary, 0.14f),
            brand = Brush.linearGradient(listOf(scheme.primary, a.secondary)),
            danger = Brush.linearGradient(listOf(Color(0xFFD6334F), Color(0xFFEA6A3C))),
            isDark = false
        )
    }

@Composable
fun AgentTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    accent: AccentPalette = AccentPalette.VIOLET,
    fontScale: Float = 1f,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    val scheme = remember(darkTheme, accent) {
        if (darkTheme) darkScheme(accent) else lightScheme(accent)
    }
    val extended = remember(darkTheme, accent) { extendedColors(darkTheme, accent, scheme) }

    // Status bar / navigation bar-এর আইকনের রঙ যেন অ্যাপের নিজের theme-এর সাথে মেলে
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            var ctx = view.context
            while (ctx is ContextWrapper && ctx !is Activity) {
                ctx = ctx.baseContext
            }
            val window = (ctx as? Activity)?.window
            if (window != null) {
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = !darkTheme
                controller.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    val baseDensity = LocalDensity.current
    val scaledDensity = remember(baseDensity, fontScale) {
        Density(baseDensity.density, baseDensity.fontScale * fontScale)
    }

    CompositionLocalProvider(
        LocalAgentColors provides extended,
        LocalSpacing provides Spacing(),
        LocalDensity provides scaledDensity
    ) {
        MaterialTheme(
            colorScheme = scheme,
            typography = AgentTypography,
            shapes = AgentShapes,
            content = content
        )
    }
}
