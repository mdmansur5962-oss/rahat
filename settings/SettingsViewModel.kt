package com.aiagent.assistant.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiagent.assistant.data.local.AppPreferences
import com.aiagent.assistant.data.local.SettingsRepository
import com.aiagent.assistant.domain.ai.AiProviderRepository
import com.aiagent.assistant.domain.settings.AppSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** সব settings পেজ একই repository ব্যবহার করে, তাই এক পেজে বদলালে অন্য সব জায়গায় সাথে সাথে বদলায় */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SettingsRepository,
    private val prefs: AppPreferences,
    aiProviderRepository: AiProviderRepository
) : ViewModel() {

    val settings: StateFlow<AppSettings> = repository.settings
        .stateIn(viewModelScope, SharingStarted.Eagerly, AppSettings())

    val userName: StateFlow<String> = prefs.userName
        .stateIn(viewModelScope, SharingStarted.Eagerly, "")

    /** "Groq · llama-3.3..." এর মতো ডিফল্ট provider-এর সারাংশ; না থাকলে null */
    val aiSummary: StateFlow<String?> = combine(
        aiProviderRepository.providers,
        aiProviderRepository.routing
    ) { providers, routing ->
        providers.firstOrNull { it.id == routing.defaultId }?.let { "${it.name} · ${it.model}" }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun update(transform: (AppSettings) -> AppSettings) {
        viewModelScope.launch { repository.update(transform) }
    }

    fun setUserName(name: String) {
        viewModelScope.launch { prefs.setUserName(name.trim().take(30)) }
    }
}
