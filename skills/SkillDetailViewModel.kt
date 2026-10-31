package com.aiagent.assistant.presentation.skills

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiagent.assistant.data.local.CustomSkillRepository
import com.aiagent.assistant.service.skills.AppSkill
import com.aiagent.assistant.service.skills.PackageUtil
import com.aiagent.assistant.service.skills.SkillExecutor
import com.aiagent.assistant.service.skills.SkillRegistry
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PendingSkillRun(
    val actionId: String,
    val params: Map<String, String>,
    val message: String
)

data class SkillDetailState(
    val skill: AppSkill? = null,
    val installedPackage: String? = null,
    val running: Boolean = false,
    val result: String = "",
    val resultOk: Boolean = true,
    val pending: PendingSkillRun? = null,
    val deleted: Boolean = false
)

@HiltViewModel
class SkillDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @ApplicationContext private val appContext: Context,
    private val registry: SkillRegistry,
    private val executor: SkillExecutor,
    private val customRepository: CustomSkillRepository
) : ViewModel() {

    private val skillId: String = savedStateHandle.get<String>("skillId").orEmpty()

    private val _state = MutableStateFlow(SkillDetailState())
    val state: StateFlow<SkillDetailState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            registry.skills.collect { list ->
                val skill = list.firstOrNull { it.id == skillId }
                _state.update {
                    it.copy(
                        skill = skill,
                        installedPackage = skill?.let { s -> PackageUtil.firstInstalled(appContext, s.packages) }
                    )
                }
            }
        }
    }

    fun run(actionId: String, params: Map<String, String>, confirmed: Boolean = false) {
        viewModelScope.launch {
            _state.update { it.copy(running = true, result = "") }
            val outcome = executor.run(skillId, actionId, params, confirmed)
            _state.update {
                it.copy(
                    running = false,
                    result = outcome.message,
                    resultOk = outcome.success,
                    pending = if (outcome.needsConfirmation) PendingSkillRun(actionId, params, outcome.message) else null
                )
            }
        }
    }

    fun confirmPending(accept: Boolean) {
        val pending = _state.value.pending ?: return
        _state.update { it.copy(pending = null) }
        if (accept) {
            run(pending.actionId, pending.params, confirmed = true)
        } else {
            _state.update { it.copy(result = "Cancelled · বাতিল করা হয়েছে", resultOk = false) }
        }
    }

    fun delete() {
        val skill = _state.value.skill ?: return
        if (skill.builtIn) return
        viewModelScope.launch {
            customRepository.delete(skill.id)
            _state.update { it.copy(deleted = true) }
        }
    }
}
