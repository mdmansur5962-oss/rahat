package com.aiagent.assistant.presentation.skills

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aiagent.assistant.presentation.components.AgentDialog
import com.aiagent.assistant.presentation.components.AgentTextField
import com.aiagent.assistant.presentation.components.ConfirmActionDialog
import com.aiagent.assistant.presentation.components.GlassButton
import com.aiagent.assistant.presentation.components.GlassCard
import com.aiagent.assistant.presentation.components.GradientButton
import com.aiagent.assistant.presentation.components.IconTile
import com.aiagent.assistant.presentation.components.SectionHeader
import com.aiagent.assistant.presentation.components.StatusPill
import com.aiagent.assistant.presentation.settings.SettingsPage
import com.aiagent.assistant.presentation.theme.AgentDesign
import com.aiagent.assistant.service.skills.ActionRisk
import com.aiagent.assistant.service.skills.SkillAction

@Composable
fun SkillDetailScreen(
    onBack: () -> Unit,
    onEdit: (String) -> Unit,
    viewModel: SkillDetailViewModel = hiltViewModel()
) {
    val cs = MaterialTheme.colorScheme
    val state by viewModel.state.collectAsStateWithLifecycle()
    val skill = state.skill

    var tryAction by remember { mutableStateOf<SkillAction?>(null) }
    var confirmDelete by remember { mutableStateOf(false) }

    LaunchedEffect(state.deleted) {
        if (state.deleted) onBack()
    }

    SettingsPage(
        title = skill?.name ?: "Skill",
        subtitle = skill?.description,
        onBack = onBack
    ) {
        if (skill == null) {
            Text(
                text = "Skill not found · skill পাওয়া যায়নি",
                style = MaterialTheme.typography.bodyMedium,
                color = cs.onSurfaceVariant
            )
            return@SettingsPage
        }
        val (icon, tone) = skillVisual(skill.id)

        // ───── overview ─────
        GlassCard {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                IconTile(icon = icon, tone = tone, size = 52.dp)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        StatusPill(
                            text = if (skill.builtIn) "Built-in" else "Custom",
                            color = if (skill.builtIn) cs.secondary else cs.tertiary
                        )
                        StatusPill(
                            text = if (state.installedPackage != null) "Installed" else "Not installed",
                            color = if (state.installedPackage != null) AgentDesign.colors.success else cs.error
                        )
                        if (skill.loginRequired) StatusPill(text = "Login", color = AgentDesign.colors.warning)
                    }
                    Text(
                        text = state.installedPackage ?: skill.packages.firstOrNull().orEmpty(),
                        style = MaterialTheme.typography.labelMedium,
                        color = cs.onSurfaceVariant
                    )
                }
            }
            if (skill.aliases.isNotEmpty()) {
                Text(
                    text = "Also called · অন্য নাম: ${skill.aliases.joinToString(", ")}",
                    style = MaterialTheme.typography.bodySmall,
                    color = cs.onSurfaceVariant,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
        }

        // ───── knowledge ─────
        if (skill.knowledge.isNotBlank()) {
            SectionHeader(title = "How it works · কীভাবে কাজ করে")
            GlassCard {
                Text(
                    text = skill.knowledge,
                    style = MaterialTheme.typography.bodyMedium,
                    color = cs.onSurface
                )
            }
        }

        // ───── hints ─────
        if (skill.hints.isNotEmpty()) {
            SectionHeader(title = "Button labels · বোতামের লেখা")
            GlassCard {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    skill.hints.forEach { (concept, labels) ->
                        Text(
                            text = "• $concept: ${labels.joinToString(" | ")}",
                            style = MaterialTheme.typography.bodySmall,
                            color = cs.onSurface
                        )
                    }
                }
            }
        }

        // ───── actions ─────
        SectionHeader(title = "Actions · কাজ", subtitle = "deep link দিয়ে নির্ভরযোগ্য কাজ। \"Try\" চেপে পরীক্ষা করুন।")
        skill.actions.forEach { action ->
            GlassCard {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(action.title, style = MaterialTheme.typography.titleSmall, color = cs.onSurface)
                        Text(
                            text = action.id + "(" + action.params.joinToString(", ") { it.name } + ")",
                            style = MaterialTheme.typography.labelMedium,
                            color = cs.secondary
                        )
                    }
                    if (action.risk == ActionRisk.CONFIRM) {
                        StatusPill(text = "Confirm", color = AgentDesign.colors.warning)
                    }
                }
                Text(
                    text = action.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = cs.onSurfaceVariant,
                    modifier = Modifier.padding(top = 6.dp, bottom = 10.dp)
                )
                GlassButton(
                    text = "Try · পরীক্ষা",
                    icon = Icons.Rounded.PlayArrow,
                    enabled = !state.running,
                    onClick = {
                        if (action.params.isEmpty()) {
                            viewModel.run(action.id, emptyMap())
                        } else {
                            tryAction = action
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // ───── result ─────
        if (state.result.isNotBlank()) {
            GlassCard(highlighted = state.resultOk) {
                Text(
                    text = (if (state.resultOk) "✓ " else "✗ ") + state.result,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (state.resultOk) AgentDesign.colors.success else cs.error
                )
            }
        }

        // ───── custom skill management ─────
        if (!skill.builtIn) {
            GlassButton(
                text = "Edit · সম্পাদনা",
                icon = Icons.Rounded.Edit,
                onClick = { onEdit(skill.id) },
                modifier = Modifier.fillMaxWidth()
            )
            GradientButton(
                text = "Delete skill · মুছুন",
                icon = Icons.Rounded.Delete,
                destructive = true,
                onClick = { confirmDelete = true },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    // ───── try dialog ─────
    val toTry = tryAction
    if (toTry != null) {
        val values = remember(toTry) { mutableStateMapOf<String, String>() }
        AgentDialog(
            title = toTry.title,
            subtitle = toTry.description,
            onDismiss = { tryAction = null },
            confirmText = "Run · চালান",
            onConfirm = {
                viewModel.run(toTry.id, values.toMap())
                tryAction = null
            },
            dismissText = "Cancel · বাতিল"
        ) {
            Column(
                modifier = Modifier
                    .heightIn(max = 320.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                toTry.params.forEach { param ->
                    AgentTextField(
                        value = values[param.name].orEmpty(),
                        onValueChange = { values[param.name] = it },
                        label = param.name + if (param.required) "" else " (optional)",
                        supportingText = param.description,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }

    val pending = state.pending
    if (pending != null) {
        ConfirmActionDialog(
            title = "Confirm this action? · নিশ্চিত করবেন?",
            message = pending.message,
            confirmText = "Do it · করুন",
            destructive = true,
            onConfirm = { viewModel.confirmPending(true) },
            onDismiss = { viewModel.confirmPending(false) }
        )
    }

    if (confirmDelete) {
        ConfirmActionDialog(
            title = "Delete this skill? · মুছবেন?",
            message = "এই custom skill ফোন থেকে মুছে যাবে।",
            confirmText = "Delete · মুছুন",
            destructive = true,
            onConfirm = {
                confirmDelete = false
                viewModel.delete()
            },
            onDismiss = { confirmDelete = false }
        )
    }
}
