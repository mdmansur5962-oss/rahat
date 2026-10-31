package com.aiagent.assistant.presentation.skills

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.aiagent.assistant.presentation.components.GlassButton
import com.aiagent.assistant.presentation.components.GlassCard
import com.aiagent.assistant.presentation.components.GradientButton
import com.aiagent.assistant.presentation.components.SectionHeader
import com.aiagent.assistant.presentation.components.ShimmerBox
import com.aiagent.assistant.presentation.settings.SettingsPage

/** নিজের skill বানানোর পেজ: অ্যাপ বাছুন, ডাকনাম দিন, টিপস লিখুন, আর deep link দিয়ে নিজের কাজ যোগ করুন */
@Composable
fun SkillEditorScreen(
    onBack: () -> Unit,
    viewModel: SkillEditorViewModel = hiltViewModel()
) {
    val cs = MaterialTheme.colorScheme
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.saved) {
        if (state.saved) onBack()
    }

    SettingsPage(
        title = if (state.isNew) "New skill · নতুন skill" else "Edit skill · সম্পাদনা",
        subtitle = "assistant-কে একটা অ্যাপ চেনান",
        onBack = onBack
    ) {
        if (state.loading) {
            ShimmerBox(modifier = Modifier.fillMaxWidth().height(60.dp))
            ShimmerBox(modifier = Modifier.fillMaxWidth().height(60.dp))
            return@SettingsPage
        }

        // ───── app ─────
        SectionHeader(title = "App · অ্যাপ")
        GlassCard {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                AgentTextField(
                    value = state.name,
                    onValueChange = { v -> viewModel.edit { it.copy(name = v.take(40)) } },
                    label = "Name · নাম",
                    modifier = Modifier.fillMaxWidth()
                )
                AgentTextField(
                    value = state.aliases,
                    onValueChange = { v -> viewModel.edit { it.copy(aliases = v.take(200)) } },
                    label = "Other names · ডাকনাম (কমা দিয়ে)",
                    supportingText = "যেমন: ফোনপে, phonepe, পে",
                    modifier = Modifier.fillMaxWidth()
                )
                AgentTextField(
                    value = state.packages,
                    onValueChange = { v -> viewModel.edit { it.copy(packages = v.take(300)) } },
                    label = "Package name(s) · প্যাকেজ (কমা দিয়ে)",
                    supportingText = "নিচের বোতামে ফোনের অ্যাপ থেকে বেছে নিন",
                    modifier = Modifier.fillMaxWidth()
                )
                GlassButton(
                    text = if (state.loadingApps) "Loading… · আনা হচ্ছে" else "Pick an installed app · ইনস্টল করা অ্যাপ বাছুন",
                    icon = Icons.Rounded.Apps,
                    enabled = !state.loadingApps,
                    onClick = { viewModel.showAppPicker() },
                    modifier = Modifier.fillMaxWidth()
                )
                AgentTextField(
                    value = state.description,
                    onValueChange = { v -> viewModel.edit { it.copy(description = v.take(160)) } },
                    label = "What is it for? · কী কাজে লাগে",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // ───── knowledge ─────
        SectionHeader(title = "Knowledge · জ্ঞান")
        GlassCard {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                AgentTextField(
                    value = state.knowledge,
                    onValueChange = { v -> viewModel.edit { it.copy(knowledge = v.take(2000)) } },
                    label = "How to use it · কীভাবে চালাতে হয়",
                    supportingText = "স্ক্রিনের কোথায় কী আছে, কোন ধাপের পর কোনটা। AI এটা পড়ে কাজ করবে।",
                    singleLine = false,
                    minLines = 4,
                    maxLines = 10,
                    modifier = Modifier.fillMaxWidth()
                )
                AgentTextField(
                    value = state.hints,
                    onValueChange = { v -> viewModel.edit { it.copy(hints = v.take(1500)) } },
                    label = "Button labels · বোতামের লেখা",
                    supportingText = "প্রতি লাইনে একটা: ধারণা = লেখা১ | লেখা২ (যেমন: export = Export | এক্সপোর্ট)",
                    singleLine = false,
                    minLines = 3,
                    maxLines = 8,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // ───── link actions ─────
        SectionHeader(title = "Actions · কাজ (deep link)")
        Text(
            text = "লিংকে {নাম} লিখলে সেখানে মান বসে। যেমন: https://example.com/search?q={query}। কল, SMS, mail ও file ধরনের লিংক চলে না, আর ব্যাংকিং সাইটও না।",
            style = MaterialTheme.typography.bodySmall,
            color = cs.onSurfaceVariant
        )
        state.links.forEachIndexed { index, form ->
            GlassCard {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Action ${index + 1}",
                            style = MaterialTheme.typography.titleSmall,
                            color = cs.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { viewModel.removeLink(index) }) {
                            Icon(Icons.Rounded.Delete, contentDescription = "Remove", tint = cs.error)
                        }
                    }
                    AgentTextField(
                        value = form.title,
                        onValueChange = { v -> viewModel.setLink(index, form.copy(title = v.take(40))) },
                        label = "Title · শিরোনাম (যেমন: Search)",
                        modifier = Modifier.fillMaxWidth()
                    )
                    AgentTextField(
                        value = form.template,
                        onValueChange = { v -> viewModel.setLink(index, form.copy(template = v.take(500))) },
                        label = "Link · লিংক",
                        modifier = Modifier.fillMaxWidth()
                    )
                    AgentTextField(
                        value = form.description,
                        onValueChange = { v -> viewModel.setLink(index, form.copy(description = v.take(120))) },
                        label = "Description · বিবরণ (ঐচ্ছিক)",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
        GlassButton(
            text = "Add action · কাজ যোগ করুন",
            icon = Icons.Rounded.Add,
            enabled = state.links.size < 8,
            onClick = { viewModel.addLink() },
            modifier = Modifier.fillMaxWidth()
        )

        val error = state.error
        if (error != null) {
            Text(text = error, style = MaterialTheme.typography.bodyMedium, color = cs.error)
        }
        GradientButton(
            text = "Save skill · সংরক্ষণ করুন",
            icon = Icons.Rounded.Save,
            onClick = { viewModel.save() },
            modifier = Modifier.fillMaxWidth()
        )
    }

    // ───── installed apps picker ─────
    val apps = state.apps
    if (apps != null) {
        var query by remember { mutableStateOf("") }
        val filtered = remember(apps, query) {
            if (query.isBlank()) apps else apps.filter {
                it.label.contains(query.trim(), ignoreCase = true) || it.packageName.contains(query.trim(), ignoreCase = true)
            }
        }
        AgentDialog(
            title = "Choose an app · অ্যাপ বাছুন",
            subtitle = "${apps.size} apps",
            onDismiss = { viewModel.hideAppPicker() },
            dismissText = "Close · বন্ধ"
        ) {
            AgentTextField(
                value = query,
                onValueChange = { query = it },
                label = "Search · খুঁজুন",
                leadingIcon = Icons.Rounded.Search,
                modifier = Modifier.fillMaxWidth()
            )
            LazyColumn(
                modifier = Modifier.heightIn(max = 320.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                items(filtered, key = { it.packageName }) { app ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.pickApp(app) }
                            .padding(horizontal = 8.dp, vertical = 10.dp)
                    ) {
                        Text(app.label, style = MaterialTheme.typography.bodyMedium, color = cs.onSurface)
                        Text(app.packageName, style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
