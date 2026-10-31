package com.aiagent.assistant.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush

@Composable
fun agentBackgroundBrush(): Brush {
    val tint = AgentDesign.colors.backgroundTint
    val base = MaterialTheme.colorScheme.background
    return Brush.verticalGradient(listOf(tint, base))
}

@Composable
fun agentAccentBrush(): Brush = AgentDesign.colors.brand
