package com.aiagent.assistant.presentation.permission

import androidx.lifecycle.viewModelScope
import com.aiagent.assistant.core.mvi.BaseViewModel
import com.aiagent.assistant.core.mvi.UiEffect
import com.aiagent.assistant.core.mvi.UiEvent
import com.aiagent.assistant.core.mvi.UiState
import com.aiagent.assistant.data.local.AppPreferences
import com.aiagent.assistant.data.permission.PermissionChecker
import com.aiagent.assistant.domain.permission.PermissionCatalog
import com.aiagent.assistant.domain.permission.PermissionDef
import com.aiagent.assistant.domain.permission.PermissionId
import com.aiagent.assistant.domain.permission.PermissionKind
import com.aiagent.assistant.domain.permission.PermissionStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

data class PermissionItemState(
    val def: PermissionDef,
    val status: PermissionStatus,
    val attempted: Boolean,
    val permanentlyDenied: Boolean
)

data class CustomPermissionState(
    val name: String,
    val granted: Boolean
)

data class PermissionUiState(
    val items: List<PermissionItemState> = emptyList(),
    val customItems: List<CustomPermissionState> = emptyList(),
    val guidedActive: Boolean = false,
    val guidedCurrent: PermissionId? = null,
    val guidedIndex: Int = 0,
    val guidedTotal: Int = 0
) : UiState {
    val required: List<PermissionItemState> get() = items.filter { it.def.required }
    val requiredGranted: Int get() = required.count { it.status.isSettled }
    val allRequiredGranted: Boolean get() = required.isNotEmpty() && requiredGranted == required.size
}

sealed interface PermissionEvent : UiEvent {
    data object ScreenResumed : PermissionEvent
    data object SettingsLaunched : PermissionEvent
    data object OpenAppDetails : PermissionEvent
    data object StopGuided : PermissionEvent
    data class Enable(val id: PermissionId) : PermissionEvent
    data class OpenSettings(val id: PermissionId) : PermissionEvent
    data class RuntimeResult(
        val granted: Map<String, Boolean>,
        val permanentlyDenied: Set<String>
    ) : PermissionEvent
    data class StartGuided(val includeOptional: Boolean) : PermissionEvent
    data class AddCustom(val name: String) : PermissionEvent
    data class RemoveCustom(val name: String) : PermissionEvent
    data class EnableCustom(val name: String) : PermissionEvent
}

sealed interface PermissionEffect : UiEffect {
    data class RequestRuntime(val permissions: List<String>) : PermissionEffect
    data class OpenSettings(val id: PermissionId) : PermissionEffect
    data object OpenAppDetails : PermissionEffect
    data class ShowMessage(val message: String) : PermissionEffect
}

@HiltViewModel
class PermissionViewModel @Inject constructor(
    private val checker: PermissionChecker,
    private val prefs: AppPreferences
) : BaseViewModel<PermissionUiState, PermissionEvent, PermissionEffect>(PermissionUiState()) {

    private val attempted = mutableSetOf<PermissionId>()
    private val permanentlyDenied = mutableSetOf<PermissionId>()
    private var customNames: Set<String> = emptySet()

    private val queue = ArrayDeque<PermissionId>()
    private var pendingId: PermissionId? = null
    private var awaitingSettingsReturn = false

    init {
        refresh()
        viewModelScope.launch {
            prefs.customPermissions.collect { names ->
                customNames = names
                refresh()
            }
        }
    }

    override fun onEvent(event: PermissionEvent) {
        when (event) {
            PermissionEvent.ScreenResumed -> onResumed()
            PermissionEvent.SettingsLaunched -> {
                if (currentState.guidedActive && pendingId != null) awaitingSettingsReturn = true
            }
            PermissionEvent.OpenAppDetails -> sendEffect(PermissionEffect.OpenAppDetails)
            PermissionEvent.StopGuided -> stopGuided()
            is PermissionEvent.Enable -> enable(event.id)
            is PermissionEvent.OpenSettings -> sendEffect(PermissionEffect.OpenSettings(event.id))
            is PermissionEvent.RuntimeResult -> onRuntimeResult(event)
            is PermissionEvent.StartGuided -> startGuided(event.includeOptional)
            is PermissionEvent.AddCustom -> addCustom(event.name)
            is PermissionEvent.RemoveCustom -> viewModelScope.launch { prefs.removeCustomPermission(event.name) }
            is PermissionEvent.EnableCustom -> {
                pendingId = null
                sendEffect(PermissionEffect.RequestRuntime(listOf(event.name)))
            }
        }
    }

    // ───────────── status refresh ─────────────

    private fun refresh() {
        val items = PermissionCatalog.all.map { def ->
            PermissionItemState(
                def = def,
                status = checker.status(def),
                attempted = def.id in attempted,
                permanentlyDenied = def.id in permanentlyDenied
            )
        }
        val custom = customNames.sorted().map { name ->
            CustomPermissionState(name = name, granted = checker.isGranted(name))
        }
        setState { copy(items = items, customItems = custom) }
    }

    private fun onResumed() {
        if (awaitingSettingsReturn) {
            awaitingSettingsReturn = false
            pendingId = null
            refresh()
            if (currentState.guidedActive) advanceGuided()
        } else {
            refresh()
        }
    }

    // ───────────── single permission ─────────────

    private fun enable(id: PermissionId) {
        val def = PermissionCatalog.byId(id)
        when (def.kind) {
            PermissionKind.RUNTIME -> {
                if (id in permanentlyDenied) {
                    sendEffect(
                        PermissionEffect.ShowMessage(
                            "Android আর dialog দেখাবে না। খোলা পেজে Permissions থেকে নিজে চালু করুন। / " +
                                "Android will not show the dialog again. Enable it in Permissions on the page that opens."
                        )
                    )
                    sendEffect(PermissionEffect.OpenAppDetails)
                    return
                }
                val perms = checker.runtimeToRequest(def)
                if (perms.isEmpty()) {
                    refresh()
                    return
                }
                pendingId = id
                sendEffect(PermissionEffect.RequestRuntime(perms))
            }
            PermissionKind.SPECIAL -> {
                attempted += id
                refresh()
                sendEffect(PermissionEffect.OpenSettings(id))
            }
            else -> Unit
        }
    }

    private fun onRuntimeResult(event: PermissionEvent.RuntimeResult) {
        val id = pendingId
        pendingId = null
        if (id != null) {
            attempted += id
            val ok = checker.status(PermissionCatalog.byId(id)).isSettled
            if (!ok && event.permanentlyDenied.isNotEmpty()) {
                permanentlyDenied += id
            } else {
                permanentlyDenied -= id
            }
        }
        refresh()
        if (id != null && currentState.guidedActive) advanceGuided()
    }

    // ───────────── guided flow ("Enable All") ─────────────

    private fun startGuided(includeOptional: Boolean) {
        refresh()
        val todo = PermissionCatalog.all
            .filter { def ->
                (def.required || includeOptional) &&
                    (def.kind == PermissionKind.RUNTIME || def.kind == PermissionKind.SPECIAL) &&
                    !checker.status(def).isSettled
            }
            // আগে ছোট dialog-গুলো, পরে Settings page-গুলো
            .sortedBy { if (it.kind == PermissionKind.RUNTIME) 0 else 1 }
            .map { it.id }

        if (todo.isEmpty()) {
            sendEffect(
                PermissionEffect.ShowMessage(
                    "সব permission আগেই দেওয়া আছে ✓ / Everything is already enabled ✓"
                )
            )
            return
        }
        queue.clear()
        queue.addAll(todo)
        setState { copy(guidedActive = true, guidedTotal = todo.size, guidedIndex = 0, guidedCurrent = null) }
        advanceGuided()
    }

    private fun advanceGuided() {
        while (true) {
            val id = queue.removeFirstOrNull()
            if (id == null) {
                finishGuided()
                return
            }
            val def = PermissionCatalog.byId(id)
            if (checker.status(def).isSettled) continue
            if (id == PermissionId.BACKGROUND_LOCATION && !checker.status(PermissionId.LOCATION).isSettled) continue

            when (def.kind) {
                PermissionKind.RUNTIME -> {
                    if (id in permanentlyDenied) continue
                    val perms = checker.runtimeToRequest(def)
                    if (perms.isEmpty()) continue
                    pendingId = id
                    setState { copy(guidedCurrent = id, guidedIndex = guidedTotal - queue.size) }
                    sendEffect(PermissionEffect.RequestRuntime(perms))
                    return
                }
                PermissionKind.SPECIAL -> {
                    pendingId = id
                    attempted += id
                    refresh()
                    setState { copy(guidedCurrent = id, guidedIndex = guidedTotal - queue.size) }
                    sendEffect(PermissionEffect.OpenSettings(id))
                    return
                }
                else -> continue
            }
        }
    }

    private fun finishGuided() {
        pendingId = null
        awaitingSettingsReturn = false
        refresh()
        setState { copy(guidedActive = false, guidedCurrent = null, guidedIndex = 0, guidedTotal = 0) }
        val missing = currentState.items.count { it.def.required && !it.status.isSettled }
        sendEffect(
            PermissionEffect.ShowMessage(
                if (missing == 0) {
                    "সব প্রয়োজনীয় permission চালু ✓ / All required permissions enabled ✓"
                } else {
                    "কিছু permission বাকি আছে। কার্ডের Enable বা Settings বাটন দিয়ে চালু করুন। / " +
                        "Some permissions are still off. Use the Enable or Settings buttons on the cards."
                }
            )
        )
    }

    private fun stopGuided() {
        queue.clear()
        pendingId = null
        awaitingSettingsReturn = false
        setState { copy(guidedActive = false, guidedCurrent = null, guidedIndex = 0, guidedTotal = 0) }
    }

    // ───────────── custom (dynamic) permissions ─────────────

    private fun addCustom(raw: String) {
        val name = raw.trim()
        if (!PERMISSION_NAME_REGEX.matches(name)) {
            sendEffect(
                PermissionEffect.ShowMessage(
                    "নামটা ঠিক নয়। উদাহরণ: android.permission.READ_CALL_LOG / Invalid name. Example: android.permission.READ_CALL_LOG"
                )
            )
            return
        }
        if (!checker.isDeclared(name)) {
            sendEffect(
                PermissionEffect.ShowMessage(
                    "এই permission AndroidManifest.xml-এ declare করা নেই, তাই Android এটা দেবে না। " +
                        "নামটা আমাকে জানান, আমি manifest-এ যোগ করে দেব। / " +
                        "This permission is not declared in AndroidManifest.xml, so Android will refuse it."
                )
            )
            return
        }
        viewModelScope.launch { prefs.addCustomPermission(name) }
    }

    private companion object {
        val PERMISSION_NAME_REGEX = Regex("^[A-Za-z][A-Za-z0-9_]*(\\.[A-Za-z0-9_]+)+$")
    }
}
