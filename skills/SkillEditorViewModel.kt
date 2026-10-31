package com.aiagent.assistant.presentation.skills

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiagent.assistant.data.local.CustomSkillRepository
import com.aiagent.assistant.service.accessibility.SafetyPolicy
import com.aiagent.assistant.service.skills.CustomLink
import com.aiagent.assistant.service.skills.CustomLinkRules
import com.aiagent.assistant.service.skills.CustomSkillSpec
import com.aiagent.assistant.service.skills.PackageUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class LinkForm(
    val title: String = "",
    val template: String = "",
    val description: String = ""
)

data class SkillEditorState(
    val loading: Boolean = true,
    val isNew: Boolean = true,
    val id: String = "",
    val name: String = "",
    val aliases: String = "",
    val packages: String = "",
    val description: String = "",
    val knowledge: String = "",
    val hints: String = "",
    val links: List<LinkForm> = listOf(LinkForm()),
    val error: String? = null,
    val saved: Boolean = false,
    val apps: List<PackageUtil.AppInfo>? = null,
    val loadingApps: Boolean = false
)

@HiltViewModel
class SkillEditorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @ApplicationContext private val appContext: Context,
    private val repository: CustomSkillRepository
) : ViewModel() {

    private val argId: String = savedStateHandle.get<String>("skillId").orEmpty()

    private val _state = MutableStateFlow(SkillEditorState())
    val state: StateFlow<SkillEditorState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val existing = if (argId.isNotBlank()) repository.specs.first().firstOrNull { it.id == argId } else null
            _state.value = if (existing != null) {
                SkillEditorState(
                    loading = false,
                    isNew = false,
                    id = existing.id,
                    name = existing.name,
                    aliases = existing.aliases.joinToString(", "),
                    packages = existing.packages.joinToString(", "),
                    description = existing.description,
                    knowledge = existing.knowledge,
                    hints = existing.hints.entries.joinToString("\n") { "${it.key} = ${it.value.joinToString(" | ")}" },
                    links = existing.links
                        .map { LinkForm(it.title, it.template, it.description) }
                        .ifEmpty { listOf(LinkForm()) }
                )
            } else {
                SkillEditorState(loading = false, isNew = true, id = "custom_" + UUID.randomUUID().toString().take(8))
            }
        }
    }

    fun edit(transform: (SkillEditorState) -> SkillEditorState) {
        _state.update { transform(it).copy(error = null) }
    }

    fun setLink(index: Int, form: LinkForm) {
        edit { s -> s.copy(links = s.links.mapIndexed { i, old -> if (i == index) form else old }) }
    }

    fun addLink() {
        edit { s -> if (s.links.size >= 8) s else s.copy(links = s.links + LinkForm()) }
    }

    fun removeLink(index: Int) {
        edit { s ->
            val next = s.links.filterIndexed { i, _ -> i != index }
            s.copy(links = next.ifEmpty { listOf(LinkForm()) })
        }
    }

    // ───────────── installed-app picker ─────────────

    fun showAppPicker() {
        _state.update { it.copy(loadingApps = true) }
        viewModelScope.launch {
            val apps = withContext(Dispatchers.IO) { PackageUtil.launchableApps(appContext) }
            _state.update { it.copy(apps = apps, loadingApps = false) }
        }
    }

    fun hideAppPicker() {
        _state.update { it.copy(apps = null) }
    }

    fun pickApp(app: PackageUtil.AppInfo) {
        edit { s ->
            val existing = splitList(s.packages)
            val merged = (existing + app.packageName).distinct()
            s.copy(
                packages = merged.joinToString(", "),
                name = s.name.ifBlank { app.label },
                apps = null
            )
        }
    }

    // ───────────── save ─────────────

    private fun splitList(text: String): List<String> =
        text.split(',', '\n').map { it.trim() }.filter { it.isNotEmpty() }.distinct()

    private fun parseHints(text: String): Map<String, List<String>> {
        val result = linkedMapOf<String, List<String>>()
        for (line in text.lines()) {
            val index = line.indexOf('=')
            if (index <= 0) continue
            val concept = line.substring(0, index).trim().lowercase()
            val labels = line.substring(index + 1).split('|', ',').map { it.trim() }.filter { it.isNotEmpty() }
            if (concept.isNotEmpty() && labels.isNotEmpty()) result[concept] = labels
        }
        return result
    }

    fun save() {
        val s = _state.value
        val name = s.name.trim()
        if (name.isEmpty()) {
            fail("Name is required · নাম দিন")
            return
        }
        val aliases = splitList(s.aliases)
        val packages = splitList(s.packages)

        // সুরক্ষিত অ্যাপের skill বানানো যায় না (নাম, ডাকনাম বা package যাই হোক)
        val protected = (listOf(name) + aliases).mapNotNull { SafetyPolicy.blockedPackage(it.replace(" ", "")) }.firstOrNull()
            ?: packages.mapNotNull { SafetyPolicy.blockedPackage(it) }.firstOrNull()
        if (protected != null) {
            fail("Protected apps can't have skills · সুরক্ষিত অ্যাপের skill বানানো যায় না")
            return
        }
        val badPackage = packages.firstOrNull { !Regex("^[A-Za-z][A-Za-z0-9_]*(\\.[A-Za-z0-9_]+)+$").matches(it) }
        if (badPackage != null) {
            fail("Invalid package name: $badPackage")
            return
        }

        val links = mutableListOf<CustomLink>()
        s.links.forEachIndexed { index, form ->
            if (form.title.isBlank() && form.template.isBlank()) return@forEachIndexed
            if (form.title.isBlank() || form.template.isBlank()) {
                fail("Action ${index + 1}: title and link are both needed · শিরোনাম ও লিংক দুটোই লাগবে")
                return
            }
            CustomLinkRules.validate(form.template)?.let {
                fail("Action ${index + 1}: $it")
                return
            }
            val slug = form.title.lowercase().replace(Regex("[^a-z0-9]+"), "_").trim('_')
            links.add(
                CustomLink(
                    id = (if (slug.isEmpty()) "action" else slug) + "_${index + 1}",
                    title = form.title.trim(),
                    description = form.description.trim(),
                    template = form.template.trim()
                )
            )
        }

        val spec = CustomSkillSpec(
            id = s.id,
            name = name,
            aliases = aliases,
            packages = packages,
            description = s.description.trim(),
            knowledge = s.knowledge.trim(),
            hints = parseHints(s.hints),
            links = links
        )
        viewModelScope.launch {
            repository.upsert(spec)
            _state.update { it.copy(saved = true) }
        }
    }

    private fun fail(message: String) {
        _state.update { it.copy(error = message) }
    }
}
