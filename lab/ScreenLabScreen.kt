package com.aiagent.assistant.presentation.lab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Accessibility
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aiagent.assistant.presentation.components.AgentChip
import com.aiagent.assistant.presentation.components.AgentSwitchRow
import com.aiagent.assistant.presentation.components.AgentTextField
import com.aiagent.assistant.presentation.components.ConfirmActionDialog
import com.aiagent.assistant.presentation.components.GlassButton
import com.aiagent.assistant.presentation.components.GlassCard
import com.aiagent.assistant.presentation.components.GradientButton
import com.aiagent.assistant.presentation.components.SectionHeader
import com.aiagent.assistant.presentation.components.StatusPill
import com.aiagent.assistant.presentation.settings.SettingsPage
import com.aiagent.assistant.presentation.theme.AgentDesign
import com.aiagent.assistant.service.accessibility.Direction
import com.aiagent.assistant.service.accessibility.SafetyLevel
import com.aiagent.assistant.service.accessibility.UiAction

/** Accessibility Engine পরীক্ষার ঘর */
@Composable
fun ScreenLabScreen(
    onBack: () -> Unit,
    onOpenPermissions: () -> Unit,
    viewModel: ScreenLabViewModel = hiltViewModel()
) {
    val cs = MaterialTheme.colorScheme
    val state by viewModel.state.collectAsStateWithLifecycle()

    var clickText by remember { mutableStateOf("") }
    var typeText by remember { mutableStateOf("") }
    var question by remember { mutableStateOf("") }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.refreshStatus()
    }

    SettingsPage(
        title = "Screen Lab · স্ক্রিন ল্যাব",
        subtitle = "স্ক্রিন পড়া ও কাজ করার পরীক্ষা",
        onBack = onBack
    ) {
        // ───── status ─────
        GlassCard(highlighted = state.serviceEnabled) {
            Row(
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Accessibility service",
                    style = MaterialTheme.typography.titleMedium,
                    color = cs.onSurface,
                    modifier = Modifier.weight(1f)
                )
                StatusPill(
                    text = if (state.serviceEnabled) "On · চালু" else "Off · বন্ধ",
                    color = if (state.serviceEnabled) AgentDesign.colors.success else cs.error
                )
            }
            if (!state.serviceEnabled) {
                Text(
                    text = "চালু করতে নিচের বোতামে চাপুন, তারপর তালিকায় এই অ্যাপ বেছে সুইচ চালু করুন। সুইচ ধূসর হলে Permission Center-এর " +
                        "\"Switch is greyed out?\" নির্দেশ দেখুন (App info → ⋮ → Allow restricted settings)।",
                    style = MaterialTheme.typography.bodySmall,
                    color = cs.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp, bottom = 10.dp)
                )
                GradientButton(
                    text = "Open Accessibility settings · চালু করুন",
                    icon = Icons.Rounded.Accessibility,
                    onClick = { viewModel.openAccessibilitySettings() },
                    modifier = Modifier.fillMaxWidth()
                )
                GlassButton(
                    text = "Permission Center · পারমিশন সেন্টার",
                    onClick = onOpenPermissions,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )
            }
            Column(modifier = Modifier.padding(top = 12.dp)) {
                AgentSwitchRow(
                    title = "Emergency stop · জরুরি থামানো",
                    subtitle = "চালু থাকলে assistant ফোনে কোনো কাজই করবে না",
                    checked = state.emergencyStop,
                    onCheckedChange = { viewModel.toggleEmergencyStop() }
                )
            }
        }

        if (state.countdown > 0) {
            GlassCard(highlighted = true) {
                Text(
                    text = "⏱ ${state.countdown}… এখন যে অ্যাপে কাজ করাতে চান সেখানে চলে যান",
                    style = MaterialTheme.typography.titleMedium,
                    color = cs.onSurface
                )
            }
        }

        // ───── read ─────
        SectionHeader(title = "1 · Read screen · স্ক্রিন পড়া")
        GlassCard {
            Text(
                text = "বোতাম চেপে অন্য অ্যাপে যান। ৩ সেকেন্ড পরে সেই স্ক্রিনের উপাদান পড়া হবে, তারপর এখানে ফিরে ফল দেখুন।",
                style = MaterialTheme.typography.bodySmall,
                color = cs.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            GradientButton(
                text = "Read in 3s · ৩ সেকেন্ডে পড়ুন",
                icon = Icons.Rounded.Search,
                enabled = !state.busy,
                onClick = { viewModel.readScreen() },
                modifier = Modifier.fillMaxWidth()
            )
            val snap = state.snapshot
            if (snap != null) {
                Column(
                    modifier = Modifier.padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatusPill(
                            text = snap.safety.level.name,
                            color = when (snap.safety.level) {
                                SafetyLevel.SAFE -> AgentDesign.colors.success
                                SafetyLevel.SENSITIVE -> AgentDesign.colors.warning
                                SafetyLevel.BLOCKED -> cs.error
                            }
                        )
                        StatusPill(text = "${snap.elements.size} elements", color = cs.secondary)
                    }
                    Text(
                        text = snap.packageName,
                        style = MaterialTheme.typography.labelMedium,
                        color = cs.onSurfaceVariant
                    )
                    if (snap.safety.reason.isNotBlank()) {
                        Text(
                            text = snap.safety.reason,
                            style = MaterialTheme.typography.bodySmall,
                            color = cs.error
                        )
                    }
                    snap.elements.take(50).forEach { e ->
                        val flags = buildList {
                            if (e.clickable) add("click")
                            if (e.editable) add("edit")
                            if (e.scrollable) add("scroll")
                            if (e.password) add("password")
                        }.joinToString(",")
                        Text(
                            text = "#${e.id} ${e.type} \"${e.label.take(36)}\" $flags",
                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontSize = 11.sp),
                            color = cs.onSurface
                        )
                    }
                    if (snap.elements.size > 50) {
                        Text("… ${snap.elements.size - 50} more", style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant)
                    }
                }
            }
        }

        // ───── actions ─────
        SectionHeader(title = "2 · Actions · কাজ")
        GlassCard {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AgentChip(text = "Back", onClick = { viewModel.runAction(UiAction.Back, "Back", seconds = 1) })
                    AgentChip(text = "Home", onClick = { viewModel.runAction(UiAction.Home, "Home", seconds = 1) })
                    AgentChip(text = "Scroll ↓", onClick = { viewModel.runAction(UiAction.Scroll(Direction.DOWN), "Scroll down") })
                    AgentChip(text = "Scroll ↑", onClick = { viewModel.runAction(UiAction.Scroll(Direction.UP), "Scroll up") })
                    AgentChip(text = "Notifications", onClick = { viewModel.runAction(UiAction.OpenNotifications, "Notifications", seconds = 1) })
                }
                AgentTextField(
                    value = clickText,
                    onValueChange = { clickText = it },
                    label = "Click the element with this text · এই লেখার উপাদানে ক্লিক",
                    modifier = Modifier.fillMaxWidth()
                )
                GlassButton(
                    text = "Click in 3s · ৩ সেকেন্ডে ক্লিক",
                    enabled = clickText.isNotBlank() && !state.busy,
                    onClick = { viewModel.runAction(UiAction.ClickText(clickText), "Click \"$clickText\"") },
                    modifier = Modifier.fillMaxWidth()
                )
                AgentTextField(
                    value = typeText,
                    onValueChange = { typeText = it },
                    label = "Type into the selected field · বাছা ঘরে লিখুন",
                    modifier = Modifier.fillMaxWidth()
                )
                GlassButton(
                    text = "Type in 3s · ৩ সেকেন্ডে লিখুন",
                    enabled = typeText.isNotBlank() && !state.busy,
                    onClick = { viewModel.runAction(UiAction.TypeText(typeText), "Type") },
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "টাইপ করতে আগে অন্য অ্যাপের লেখার ঘরে ট্যাপ করে রাখুন। পেমেন্ট, পাঠানো, মোছা, কল ধরনের বোতামে ক্লিক করতে চাইলে নিশ্চিত করতে বলবে।",
                    style = MaterialTheme.typography.bodySmall,
                    color = cs.onSurfaceVariant
                )
            }
        }

        // ───── OCR ─────
        SectionHeader(title = "3 · OCR · ছবি থেকে লেখা")
        GlassCard {
            Text(
                text = "ফোনের ভেতরেই চলে, ইন্টারনেট লাগে না। শুধু ইংরেজি (ল্যাটিন) অক্ষর পড়তে পারে। Android 11+ লাগে।",
                style = MaterialTheme.typography.bodySmall,
                color = cs.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            GlassButton(
                text = "OCR in 3s · ৩ সেকেন্ডে",
                enabled = !state.busy,
                onClick = { viewModel.ocr() },
                modifier = Modifier.fillMaxWidth()
            )
            if (state.ocrText.isNotBlank()) {
                Text(
                    text = state.ocrText.take(1500),
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontSize = 11.sp),
                    color = cs.onSurface,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
        }

        // ───── AI vision ─────
        SectionHeader(title = "4 · Ask AI about the screen · AI-কে জিজ্ঞেস")
        GlassCard {
            Text(
                text = "⚠ স্ক্রিনের ছবি আপনার বেছে নেওয়া AI provider-কে পাঠানো হয় (শুধু এই অনুরোধে, কোথাও জমানো হয় না)। " +
                    "ব্যাংকিং, password, OTP বা কার্ডের স্ক্রিনে কখনো পাঠানো হয় না। ছবি বোঝে এমন model লাগে।",
                style = MaterialTheme.typography.bodySmall,
                color = cs.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            AgentTextField(
                value = question,
                onValueChange = { question = it },
                label = "Question · প্রশ্ন (খালি রাখলে স্ক্রিনের বর্ণনা)",
                modifier = Modifier.fillMaxWidth()
            )
            GlassButton(
                text = "Ask in 3s · ৩ সেকেন্ডে জিজ্ঞেস",
                enabled = !state.busy,
                onClick = { viewModel.analyze(question) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            )
            if (state.analysis.isNotBlank()) {
                Text(
                    text = state.analysis,
                    style = MaterialTheme.typography.bodyMedium,
                    color = cs.onSurface,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
        }

        // ───── AI-ready context ─────
        if (state.promptContext.isNotBlank()) {
            SectionHeader(title = "AI-ready screen description · AI-র জন্য বর্ণনা")
            GlassCard {
                Text(
                    text = state.promptContext.take(3000),
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontSize = 10.sp),
                    color = cs.onSurface
                )
            }
        }

        // ───── log ─────
        SectionHeader(title = "Log · ইতিহাস")
        GlassCard {
            if (state.log.isEmpty()) {
                Text("কিছু নেই · Nothing yet", style = MaterialTheme.typography.bodySmall, color = cs.onSurfaceVariant)
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    state.log.forEach { line ->
                        Text(
                            text = line,
                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontSize = 11.sp),
                            color = cs.onSurface
                        )
                    }
                }
            }
        }
    }

    val pending = state.pendingConfirm
    if (pending != null) {
        ConfirmActionDialog(
            title = "Confirm this action? · নিশ্চিত করবেন?",
            message = pending.reason,
            confirmText = "Do it · করুন",
            dismissText = "Cancel · বাতিল",
            destructive = true,
            onConfirm = { viewModel.confirmPending(true) },
            onDismiss = { viewModel.confirmPending(false) }
        )
    }
}
