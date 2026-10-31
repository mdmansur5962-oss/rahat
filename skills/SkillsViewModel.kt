package com.aiagent.assistant.presentation.skills

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiagent.assistant.service.skills.AppSkill
import com.aiagent.assistant.service.skills.PackageUtil
import com.aiagent.assistant.service.skills.SkillRegistry
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn

data class SkillRow(val skill: AppSkill, val installedPackage: String?)

@HiltViewModel
class SkillsViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    registry: SkillRegistry
) : ViewModel() {

    private val tick = MutableStateFlow(0)

    val rows: StateFlow<List<SkillRow>> = combine(registry.skills, tick) { skills, _ ->
        skills.map { SkillRow(it, PackageUtil.firstInstalled(appContext, it.packages)) }
    }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** অ্যাপ ইনস্টল/মোছার পর "Installed" অবস্থা আবার পড়ে */
    fun refresh() {
        tick.value = tick.value + 1
    }
}
