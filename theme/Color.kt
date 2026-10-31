package com.aiagent.assistant.presentation.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ───────── MYRA design থেকে নেওয়া base রঙ (গাঢ় নেভি) ─────────
val DarkBackground = Color(0xFF050A1C)
val DarkSurface = Color(0xFF0B1531)
val DarkSurfaceVariant = Color(0xFF14224A)
val DarkOutline = Color(0xFF3A4F85)
val DarkOnSurface = Color(0xFFF2F5FF)
val DarkOnSurfaceVariant = Color(0xFF8FA3D6)
val ErrorDark = Color(0xFFFF5470)

val LightBackground = Color(0xFFF3F5FF)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFE6EAFB)
val LightOutline = Color(0xFF76809F)
val LightOnSurface = Color(0xFF131A33)
val LightOnSurfaceVariant = Color(0xFF475070)
val ErrorLight = Color(0xFFBA1A3A)

/**
 * User যে accent রঙ বেছে নেবে। VIOLET হলো MYRA design-এর রঙ (বেগুনি → নীল, সাথে teal)।
 */
enum class AccentPalette(
    val nameEn: String,
    val nameBn: String,
    val primary: Color,
    val secondary: Color,
    val tertiary: Color
) {
    VIOLET("MYRA Violet", "মায়রা বেগুনি", Color(0xFF7C4DFF), Color(0xFF2F80FF), Color(0xFF1FD1B5)),
    OCEAN("Ocean", "সমুদ্র", Color(0xFF3B82F6), Color(0xFF22D3EE), Color(0xFF8B5CF6)),
    EMERALD("Emerald", "সবুজ", Color(0xFF10B981), Color(0xFF38BDF8), Color(0xFFF59E0B)),
    SUNSET("Sunset", "সূর্যাস্ত", Color(0xFFFF7849), Color(0xFFFFC857), Color(0xFFFF4D8D)),
    ROSE("Rose", "গোলাপি", Color(0xFFEC4899), Color(0xFFA78BFA), Color(0xFF22D3EE))
}

/** Feature tile আর Settings row-এর রঙিন আইকন বাক্সের রঙ (design-এর মতো) */
enum class IconTone(val top: Color, val bottom: Color) {
    PURPLE(Color(0xFF8B5CF6), Color(0xFF5B21B6)),
    TEAL(Color(0xFF2DD4BF), Color(0xFF0D9488)),
    GREEN(Color(0xFF34D399), Color(0xFF059669)),
    SKY(Color(0xFF38BDF8), Color(0xFF0284C7)),
    BLUE(Color(0xFF60A5FA), Color(0xFF2563EB)),
    ORANGE(Color(0xFFFBBF24), Color(0xFFF97316)),
    RED(Color(0xFFF87171), Color(0xFFDC2626)),
    PINK(Color(0xFFF472B6), Color(0xFFDB2777)),
    SLATE(Color(0xFF64748B), Color(0xFF334155));

    val brush: Brush get() = Brush.linearGradient(listOf(top, bottom))
}
