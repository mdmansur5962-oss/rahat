package com.aiagent.assistant.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.aiagent.assistant.presentation.theme.AgentDesign

/**
 * MYRA design-এর কার্ড: গাঢ় নেভির উপর আধা-স্বচ্ছ নীল ভরাট ও পাতলা নীলচে border।
 * tint দিলে সেই রঙের হালকা আভা ও border আসে (যেমন File Upload tile-এর teal আভা)।
 * highlighted হলে brand gradient border।
 */
@Composable
fun Modifier.glassSurface(
    shape: Shape = MaterialTheme.shapes.large,
    highlighted: Boolean = false,
    tint: Color? = null
): Modifier {
    val c = AgentDesign.colors
    val borderBrush: Brush = when {
        highlighted -> c.brand
        tint != null -> SolidColor(tint.copy(alpha = 0.40f))
        else -> SolidColor(c.glassBorder)
    }
    val fillBrush: Brush = if (tint != null) {
        Brush.linearGradient(listOf(tint.copy(alpha = 0.14f), c.glassFill))
    } else {
        Brush.linearGradient(listOf(c.glassHighlight, c.glassFill))
    }
    return this
        .clip(shape)
        .background(brush = fillBrush, shape = shape)
        .border(
            width = if (highlighted) 1.5.dp else 1.dp,
            brush = borderBrush,
            shape = shape
        )
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.large,
    highlighted: Boolean = false,
    tint: Color? = null,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val haptics = rememberAgentHaptics()
    val base = modifier.glassSurface(shape = shape, highlighted = highlighted, tint = tint)
    val clickable = if (onClick != null) {
        base.clickable {
            haptics.tick()
            onClick()
        }
    } else {
        base
    }
    Column(
        modifier = clickable.padding(contentPadding),
        content = content
    )
}
