package com.aiagent.assistant.presentation.lab

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiagent.assistant.data.permission.PermissionIntents
import com.aiagent.assistant.domain.permission.PermissionId
import com.aiagent.assistant.service.accessibility.AccessibilityEngine
import com.aiagent.assistant.service.accessibility.ActionResult
import com.aiagent.assistant.service.accessibility.ScreenContext
import com.aiagent.assistant.service.accessibility.ScreenRead
import com.aiagent.assistant.service.accessibility.UiAction
import com.aiagent.assistant.service.accessibility.UiSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PendingConfirm(val action: UiAction, val reason: String)

data class ScreenLabState(
    val serviceEnabled: Boolean = false,
    val emergencyStop: Boolean = false,
    /** শূন্য নয় মানে গোনা চলছে: এই সময়ের মধ্যে অন্য অ্যাপে চলে যান */
    val countdown: Int = 0,
    val busy: Boolean = false,
    val snapshot: UiSnapshot? = null,
    val promptContext: String = "",
    val ocrText: String = "",
    val analysis: String = "",
    val log: List<String> = emptyList(),
    val pendingConfirm: PendingConfirm? = null
)

/**
 * পরীক্ষার ঘর: গোনা শেষে (এর মধ্যে আপনি অন্য অ্যাপে যান) স্ক্রিন পড়ে, কাজ করে, OCR বা AI-কে জিজ্ঞেস করে।
 * ফল এখানে ফিরে এলে দেখা যায়।
 */
@HiltViewModel
class ScreenLabViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val engine: AccessibilityEngine
) : ViewModel() {

    private val _state = MutableStateFlow(ScreenLabState(serviceEnabled = engine.isEnabled))
    val state: StateFlow<ScreenLabState> = _state.asStateFlow()

    private var job: Job? = null
    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.US)

    init {
        viewModelScope.launch {
            engine.emergencyStop.collect { stop -> _state.update { it.copy(emergencyStop = stop) } }
        }
    }

    fun refreshStatus() {
        _state.update { it.copy(serviceEnabled = engine.isEnabled) }
    }

    fun openAccessibilitySettings() {
        val intent = PermissionIntents.settingsFor(appContext, PermissionId.ACCESSIBILITY)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            appContext.startActivity(intent)
        } catch (e: Exception) {
            log("Could not open Accessibility settings")
        }
    }

    fun toggleEmergencyStop() {
        engine.setEmergencyStop(!_state.value.emergencyStop)
    }

    // ───────────── actions (all start after a countdown) ─────────────

    fun readScreen(seconds: Int = 3) = schedule(seconds) {
        when (val read = engine.readScreen()) {
            ScreenRead.NotEnabled -> log("Accessibility is not enabled")
            ScreenRead.NoWindow -> log("No window to read (try again)")
            is ScreenRead.Ok -> {
                val snap = read.snapshot
                _state.update {
                    it.copy(snapshot = snap, promptContext = ScreenContext.promptContext(snap))
                }
                log("Read ${snap.elements.size} elements from ${snap.packageName} [${snap.safety.level}]")
            }
        }
    }

    fun runAction(action: UiAction, label: String, seconds: Int = 3) = schedule(seconds) {
        perform(action, label, confirmed = false)
    }

    fun confirmPending(accept: Boolean) {
        val pending = _state.value.pendingConfirm ?: return
        _state.update { it.copy(pendingConfirm = null) }
        if (!accept) {
            log("Cancelled: ${pending.reason}")
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(busy = true) }
            perform(pending.action, "confirmed action", confirmed = true)
            _state.update { it.copy(busy = false) }
        }
    }

    fun ocr(seconds: Int = 3) = schedule(seconds) {
        val result = engine.ocr()
        if (result.ok) {
            _state.update { it.copy(ocrText = result.lines.joinToString("\n") { line -> line.text }) }
            log("OCR: ${result.lines.size} lines")
        } else {
            log("OCR: ${result.message}")
        }
    }

    fun analyze(question: String, seconds: Int = 3) = schedule(seconds) {
        val q = question.ifBlank { "Describe what is on this screen." }
        val result = engine.analyzeScreen(q)
        if (result.ok) {
            _state.update { it.copy(analysis = result.text) }
            log("AI answered")
        } else {
            log("AI: ${result.text}")
        }
    }

    // ───────────── internals ─────────────

    private suspend fun perform(action: UiAction, label: String, confirmed: Boolean) {
        val result: ActionResult = engine.perform(action, confirmed)
        when {
            result.needsConfirmation -> {
                _state.update { it.copy(pendingConfirm = PendingConfirm(action, result.message)) }
                log("Needs confirmation: ${result.message}")
            }
            result.success -> log("OK · $label: ${result.message}")
            else -> log("FAILED · $label: ${result.message}")
        }
    }

    private fun schedule(seconds: Int, block: suspend () -> Unit) {
        job?.cancel()
        job = viewModelScope.launch {
            _state.update { it.copy(busy = true, serviceEnabled = engine.isEnabled) }
            for (i in seconds downTo 1) {
                _state.update { it.copy(countdown = i) }
                delay(1000)
            }
            _state.update { it.copy(countdown = 0) }
            try {
                block()
            } finally {
                _state.update { it.copy(busy = false, countdown = 0, serviceEnabled = engine.isEnabled) }
            }
        }
    }

    private fun log(message: String) {
        val line = "${timeFormat.format(Date())}  $message"
        _state.update { it.copy(log = (listOf(line) + it.log).take(40)) }
    }
}
