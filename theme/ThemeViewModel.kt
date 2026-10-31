package com.aiagent.assistant.presentation.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiagent.assistant.data.local.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val prefs: AppPreferences
) : ViewModel() {

    val themeMode: StateFlow<ThemeMode> = prefs.themeModeName
        .map { name -> ThemeMode.entries.firstOrNull { it.name == name } ?: ThemeMode.SYSTEM }
        .stateIn(viewModelScope, SharingStarted.Eagerly, ThemeMode.SYSTEM)

    val accent: StateFlow<AccentPalette> = prefs.accentName
        .map { name -> AccentPalette.entries.firstOrNull { it.name == name } ?: AccentPalette.VIOLET }
        .stateIn(viewModelScope, SharingStarted.Eagerly, AccentPalette.VIOLET)

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { prefs.setThemeModeName(mode.name) }
    }

    fun setAccent(palette: AccentPalette) {
        viewModelScope.launch { prefs.setAccentName(palette.name) }
    }
}
