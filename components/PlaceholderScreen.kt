package com.aiagent.assistant.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aiagent.assistant.R

/** যে পেজ এখনো বানানো হয়নি তার জায়গা। কোন ধাপে আসবে সেটাও দেখায়। */
@Composable
fun PlaceholderScreen(
    title: String,
    modifier: Modifier = Modifier,
    stepLabel: String? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    onBack: (() -> Unit)? = null
) {
    val cs = MaterialTheme.colorScheme
    GradientBackground(modifier = modifier) {
        if (onBack != null) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = cs.onBackground
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
        ) {
            RobotMascot(Modifier.size(96.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = cs.onBackground,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.placeholder_coming_soon),
                style = MaterialTheme.typography.bodyMedium,
                color = cs.onSurfaceVariant
            )
            if (stepLabel != null) {
                StatusPill(text = stepLabel, color = cs.primary)
            }
            if (actionLabel != null && onAction != null) {
                GlassButton(
                    text = actionLabel,
                    onClick = onAction,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
