package com.aiagent.assistant.presentation.voice

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.SystemClock
import android.speech.SpeechRecognizer
import androidx.core.content.ContextCompat
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.aiagent.assistant.core.mvi.BaseViewModel
import com.aiagent.assistant.core.mvi.UiEffect
import com.aiagent.assistant.core.mvi.UiEvent
import com.aiagent.assistant.core.mvi.UiState
import com.aiagent.assistant.data.local.SettingsRepository
import com.aiagent.assistant.domain.ai.AiErrorKind
import com.aiagent.assistant.domain.ai.AiException
import com.aiagent.assistant.domain.chat.ChatRepository
import com.aiagent.assistant.domain.chat.ChatResponder
import com.aiagent.assistant.domain.chat.MessageRole
import com.aiagent.assistant.domain.chat.ReplyEvent
import com.aiagent.assistant.domain.settings.VoiceLanguage
import com.aiagent.assistant.domain.settings.VoiceStyle
import com.aiagent.assistant.domain.voice.SpeakableText
import com.aiagent.assistant.service.voice.SpeakResult
import com.aiagent.assistant.service.voice.SpeechInput
import com.aiagent.assistant.service.voice.SpeechOutput
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class VoicePhase { IDLE, LISTENING, THINKING, SPEAKING }

data class VoiceUiState(
    val phase: VoicePhase = VoicePhase.IDLE,
    /** ব্যবহারকারী এখন যা বলছে (আংশিক) */
    val partialText: String = "",
    val lastUserText: String = "",
    /** assistant-এর উত্তর (লেখা চলছে বা শেষ উত্তর), সাধারণ লেখায় */
    val replyText: String = "",
    val levels: List<Float> = List(WAVE_BARS) { 0f },
    val level: Float = 0f,
    val chatId: Long? = null,
    val micAvailable: Boolean = true,
    val needsPermission: Boolean = false,
    val waitingForWakeWord: Boolean = false,
    val message: String? = null,
    // settings থেকে
    val language: VoiceLanguage = VoiceLanguage.AUTO,
    val assistantName: String = "MYRA",
    val wakeWord: String = "Hey MYRA",
    val wakeWordEnabled: Boolean = false,
    val continuous: Boolean = true,
    val speakReplies: Boolean = true,
    val voiceStyle: VoiceStyle = VoiceStyle.FEMALE,
    val voiceSpeed: Float = 1f,
    val voicePitch: Float = 1.1f
) : UiState {
    companion object {
        const val WAVE_BARS = 28
    }
}

sealed interface VoiceEvent : UiEvent {
    data object ScreenOpened : VoiceEvent
    data object ScreenPaused : VoiceEvent
    data class PermissionResult(val granted: Boolean) : VoiceEvent
    data object MicTapped : VoiceEvent
    data class SetLanguage(val language: VoiceLanguage) : VoiceEvent
    data object ToggleContinuous : VoiceEvent
    data object ToggleSpeak : VoiceEvent
    data object ToggleWakeWord : VoiceEvent
    data object DismissMessage : VoiceEvent
}

sealed interface VoiceEffect : UiEffect {
    data object RequestMicPermission : VoiceEffect
    data object WakeDetected : VoiceEffect
}

/**
 * Live Voice কথোপকথন: শোনা → AI-কে জিজ্ঞেস → উত্তর জোরে পড়া → (টানা শোনা চালু থাকলে) আবার শোনা।
 * সব কথা সাধারণ চ্যাটের মতোই database-এ জমে, তাই পরে Chat/History-তে দেখা যায়।
 */
@HiltViewModel
class VoiceViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    savedStateHandle: SavedStateHandle,
    private val repository: ChatRepository,
    private val responder: ChatResponder,
    private val settingsRepository: SettingsRepository
) : BaseViewModel<VoiceUiState, VoiceEvent, VoiceEffect>(
    VoiceUiState(chatId = savedStateHandle.get<Long>("chatId")?.takeIf { it > 0 })
) {

    private var chatId: Long? = savedStateHandle.get<Long>("chatId")?.takeIf { it > 0 }
    private var opened = false
    private var closed = false
    private var expectCancel = false
    private var silentRetries = 0
    private var lastLevelUpdate = 0L
    private var generationJob: Job? = null
    private var output: SpeechOutput? = null

    private val speechInput = SpeechInput(
        appContext,
        object : SpeechInput.Callbacks {
            override fun onReady() {}
            override fun onPartial(text: String) = handlePartial(text)
            override fun onFinal(text: String) = handleFinal(text)
            override fun onError(code: Int) = handleError(code)
            override fun onLevel(level: Float) = handleLevel(level)
        }
    )

    init {
        viewModelScope.launch {
            settingsRepository.settings.collect { s ->
                setState {
                    copy(
                        language = s.voiceLanguage,
                        assistantName = s.assistantName,
                        wakeWord = s.wakeWord,
                        wakeWordEnabled = s.wakeWordEnabled,
                        continuous = s.continuousListening,
                        speakReplies = s.speakReplies,
                        voiceStyle = s.voiceStyle,
                        voiceSpeed = s.voiceSpeed,
                        voicePitch = s.voicePitch
                    )
                }
            }
        }
    }

    override fun onEvent(event: VoiceEvent) {
        when (event) {
            VoiceEvent.ScreenOpened -> onOpened()
            VoiceEvent.ScreenPaused -> stopAll()
            is VoiceEvent.PermissionResult -> onPermission(event.granted)
            VoiceEvent.MicTapped -> onMicTapped()
            is VoiceEvent.SetLanguage -> setLanguage(event.language)
            VoiceEvent.ToggleContinuous -> {
                val value = !currentState.continuous
                viewModelScope.launch { settingsRepository.update { it.copy(continuousListening = value) } }
            }
            VoiceEvent.ToggleSpeak -> {
                val value = !currentState.speakReplies
                if (!value) output?.stop()
                viewModelScope.launch { settingsRepository.update { it.copy(speakReplies = value) } }
            }
            VoiceEvent.ToggleWakeWord -> toggleWake()
            VoiceEvent.DismissMessage -> setState { copy(message = null) }
        }
    }

    // ───────────── session ─────────────

    private fun hasMicPermission(): Boolean =
        ContextCompat.checkSelfPermission(appContext, Manifest.permission.RECORD_AUDIO) ==
            PackageManager.PERMISSION_GRANTED

    private fun onOpened() {
        if (opened) return
        opened = true
        if (!speechInput.isAvailable()) {
            setState { copy(micAvailable = false) }
            return
        }
        if (!hasMicPermission()) {
            setState { copy(needsPermission = true) }
            sendEffect(VoiceEffect.RequestMicPermission)
            return
        }
        beginSession()
    }

    private fun onPermission(granted: Boolean) {
        if (granted) {
            setState { copy(needsPermission = false) }
            beginSession()
        } else {
            setState { copy(needsPermission = true) }
        }
    }

    private fun beginSession() {
        startListening(forWake = currentState.wakeWordEnabled)
    }

    private fun startListening(forWake: Boolean) {
        closed = false
        output?.stop()
        setState {
            copy(
                phase = VoicePhase.LISTENING,
                partialText = "",
                message = null,
                waitingForWakeWord = forWake,
                needsPermission = false
            )
        }
        speechInput.start(currentState.language)
    }

    private fun stopAll() {
        closed = true
        generationJob?.cancel()
        expectCancel = true
        speechInput.cancel()
        output?.stop()
        setState {
            copy(
                phase = VoicePhase.IDLE,
                partialText = "",
                level = 0f,
                levels = List(VoiceUiState.WAVE_BARS) { 0f },
                waitingForWakeWord = false
            )
        }
    }

    private fun onMicTapped() {
        when (currentState.phase) {
            VoicePhase.IDLE -> {
                if (!speechInput.isAvailable()) {
                    setState { copy(micAvailable = false) }
                } else if (!hasMicPermission()) {
                    setState { copy(needsPermission = true) }
                    sendEffect(VoiceEffect.RequestMicPermission)
                } else {
                    startListening(forWake = false)
                }
            }
            VoicePhase.LISTENING -> {
                expectCancel = true
                speechInput.cancel()
                setState { copy(phase = VoicePhase.IDLE, partialText = "", waitingForWakeWord = false) }
            }
            VoicePhase.THINKING -> {
                generationJob?.cancel()
                setState { copy(phase = VoicePhase.IDLE) }
            }
            VoicePhase.SPEAKING -> {
                // মাঝপথে থামিয়ে নিজে বলা শুরু (barge-in)
                output?.stop()
                startListening(forWake = false)
            }
        }
    }

    private fun setLanguage(language: VoiceLanguage) {
        viewModelScope.launch { settingsRepository.update { it.copy(voiceLanguage = language) } }
        if (currentState.phase == VoicePhase.LISTENING) {
            expectCancel = true
            speechInput.cancel()
            viewModelScope.launch {
                delay(250)
                if (currentState.phase == VoicePhase.LISTENING) {
                    speechInput.start(language)
                }
            }
        }
    }

    private fun toggleWake() {
        val enable = !currentState.wakeWordEnabled
        viewModelScope.launch { settingsRepository.update { it.copy(wakeWordEnabled = enable) } }
        if (enable && currentState.phase == VoicePhase.IDLE && hasMicPermission() && speechInput.isAvailable()) {
            startListening(forWake = true)
        } else if (!enable && currentState.waitingForWakeWord) {
            expectCancel = true
            speechInput.cancel()
            setState { copy(phase = VoicePhase.IDLE, waitingForWakeWord = false, partialText = "") }
        }
    }

    // ───────────── recognizer callbacks ─────────────

    private fun handleLevel(level: Float) {
        val now = SystemClock.elapsedRealtime()
        if (now - lastLevelUpdate < 70) return
        lastLevelUpdate = now
        setState {
            copy(level = level, levels = (levels + level).takeLast(VoiceUiState.WAVE_BARS))
        }
    }

    private fun handlePartial(text: String) {
        if (currentState.phase == VoicePhase.LISTENING) setState { copy(partialText = text) }
    }

    private fun handleFinal(text: String) {
        val clean = text.trim()
        setState { copy(level = 0f) }
        if (clean.isEmpty()) {
            onSilence()
            return
        }
        silentRetries = 0
        if (currentState.waitingForWakeWord) handleWake(clean) else submit(clean)
    }

    private fun handleWake(text: String) {
        val wake = currentState.wakeWord.trim()
        val index = if (wake.isEmpty()) -1 else text.indexOf(wake, ignoreCase = true)
        if (index >= 0) {
            val rest = text.substring(index + wake.length).trim().trimStart(',', '.', '!', ' ')
            sendEffect(VoiceEffect.WakeDetected)
            if (rest.length >= 2) submit(rest) else startListening(forWake = false)
        } else {
            restartWakeLater()
        }
    }

    private fun restartWakeLater() {
        viewModelScope.launch {
            delay(300)
            if (!closed && currentState.waitingForWakeWord && currentState.phase == VoicePhase.LISTENING) {
                speechInput.start(currentState.language)
            }
        }
    }

    private fun onSilence() {
        when {
            currentState.waitingForWakeWord -> restartWakeLater()
            currentState.continuous && silentRetries < 2 -> {
                silentRetries++
                startListening(forWake = false)
            }
            else -> {
                silentRetries = 0
                if (currentState.wakeWordEnabled && !closed) {
                    startListening(forWake = true)
                } else {
                    setState {
                        copy(
                            phase = VoicePhase.IDLE,
                            message = "শুনতে পাইনি, মাইকে ট্যাপ করুন · Didn't catch that, tap the mic"
                        )
                    }
                }
            }
        }
    }

    private fun handleError(code: Int) {
        when (code) {
            SpeechRecognizer.ERROR_NO_MATCH,
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> onSilence()
            SpeechRecognizer.ERROR_CLIENT -> {
                // আমরা নিজে cancel করলে এই ত্রুটি আসে, সেটা উপেক্ষা
                if (expectCancel) {
                    expectCancel = false
                } else if (currentState.phase == VoicePhase.LISTENING) {
                    setState { copy(phase = VoicePhase.IDLE) }
                }
            }
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> {
                speechInput.reset()
                viewModelScope.launch {
                    delay(400)
                    if (!closed && currentState.phase == VoicePhase.LISTENING) {
                        speechInput.start(currentState.language)
                    }
                }
            }
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> {
                setState { copy(phase = VoicePhase.IDLE, needsPermission = true) }
            }
            SpeechRecognizer.ERROR_NETWORK,
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> fail(
                "Speech service needs internet (or an offline language pack) · " +
                    "ইন্টারনেট লাগবে, বা ফোনে offline ভাষা প্যাক নামান"
            )
            SpeechRecognizer.ERROR_LANGUAGE_NOT_SUPPORTED,
            SpeechRecognizer.ERROR_LANGUAGE_UNAVAILABLE -> fail(
                "This language is not available for speech input · এই ভাষায় শোনা যাচ্ছে না। " +
                    "অন্য ভাষা বেছে নিন বা ফোনে ভাষা প্যাক নামান"
            )
            else -> fail("Voice error ($code) · ভয়েস ত্রুটি ($code)")
        }
    }

    private fun fail(message: String) {
        setState { copy(phase = VoicePhase.IDLE, message = message, waitingForWakeWord = false) }
    }

    // ───────────── ask the AI + speak ─────────────

    private fun submit(text: String) {
        generationJob?.cancel()
        setState {
            copy(
                phase = VoicePhase.THINKING,
                lastUserText = text,
                partialText = "",
                replyText = "",
                message = null,
                waitingForWakeWord = false
            )
        }
        generationJob = viewModelScope.launch {
            val buffer = StringBuilder()
            try {
                val existing = chatId
                val cid = if (existing != null) {
                    existing
                } else {
                    val created = repository.createChat(text.lineSequence().firstOrNull().orEmpty().trim().take(40))
                    chatId = created
                    setState { copy(chatId = created) }
                    created
                }
                repository.addMessage(cid, MessageRole.USER, text)
                val history = repository.getMessages(cid)
                responder.reply(history, currentState.assistantName, spoken = true).collect { event ->
                    if (event is ReplyEvent.Text) {
                        buffer.append(event.chunk)
                        setState { copy(replyText = SpeakableText.from(buffer.toString())) }
                    }
                }
                if (buffer.isBlank()) {
                    throw AiException(AiErrorKind.UNKNOWN, "Empty reply · উত্তর খালি এসেছে")
                }
                val reply = buffer.toString()
                repository.addMessage(cid, MessageRole.ASSISTANT, reply)
                deliver(reply)
            } catch (e: CancellationException) {
                throw e
            } catch (e: AiException) {
                fail(e.message.orEmpty())
            } catch (e: Exception) {
                fail("Something went wrong · কিছু একটা ভুল হয়েছে\n" + e.message.orEmpty())
            }
        }
    }

    private fun deliver(reply: String) {
        if (currentState.speakReplies) speak(reply) else afterTurn()
    }

    private fun speak(reply: String) {
        val s = currentState
        setState { copy(phase = VoicePhase.SPEAKING) }
        val engine = output ?: SpeechOutput(appContext).also { output = it }
        val result = engine.speak(reply, null, s.voiceStyle, s.voiceSpeed, s.voicePitch) {
            viewModelScope.launch { afterTurn() }
        }
        when (result) {
            SpeakResult.OK -> Unit
            SpeakResult.NOT_READY -> {
                setState { copy(message = "Voice engine is starting, try again · ভয়েস ইঞ্জিন চালু হচ্ছে, আবার চেষ্টা করুন") }
                afterTurn()
            }
            SpeakResult.LANGUAGE_MISSING -> {
                setState {
                    copy(
                        message = "This language's voice is not installed · এই ভাষার voice ফোনে নেই " +
                            "(Settings → Text-to-speech থেকে নামান)"
                    )
                }
                afterTurn()
            }
        }
    }

    private fun afterTurn() {
        if (closed) return
        if (currentState.continuous) {
            startListening(forWake = false)
        } else if (currentState.wakeWordEnabled) {
            startListening(forWake = true)
        } else {
            setState { copy(phase = VoicePhase.IDLE) }
        }
    }

    override fun onCleared() {
        closed = true
        generationJob?.cancel()
        speechInput.destroy()
        output?.shutdown()
        output = null
        super.onCleared()
    }
}
