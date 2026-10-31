package com.aiagent.assistant.presentation.navigation

import android.content.Intent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Notification বা ভাসমান বাবল থেকে অ্যাপ খুলে সরাসরি কোনো পেজে যাওয়ার অনুরোধ।
 * নিরাপত্তার জন্য শুধু নির্দিষ্ট কয়েকটি পেজে যাওয়া যায়।
 */
object LaunchRouter {

    const val EXTRA_ROUTE = "com.aiagent.assistant.EXTRA_ROUTE"

    private val allowedExact = setOf("home", "chat", "history")
    private val allowedPrefixes = listOf("voice", "chat/")

    private val _pending = MutableStateFlow<String?>(null)
    val pending: StateFlow<String?> = _pending.asStateFlow()

    fun handle(intent: Intent?) {
        val route = intent?.getStringExtra(EXTRA_ROUTE)?.trim().orEmpty()
        if (route.isEmpty()) return
        if (route in allowedExact || allowedPrefixes.any { route.startsWith(it) }) {
            _pending.value = route
        }
    }

    fun consume() {
        _pending.value = null
    }
}
