package com.aiagent.assistant.presentation.permission

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.aiagent.assistant.data.permission.PermissionIntents

internal fun Context.findActivity(): Activity? {
    var current: Context = this
    while (current is ContextWrapper) {
        if (current is Activity) return current
        current = current.baseContext
    }
    return null
}

private fun Context.launchSettings(intent: Intent) {
    val host: Context = findActivity() ?: this
    try {
        host.startActivity(intent)
    } catch (e: Exception) {
        try {
            host.startActivity(PermissionIntents.appDetails(this))
        } catch (e2: Exception) {
            Toast.makeText(this, "Settings খোলা যায়নি / Could not open Settings", Toast.LENGTH_SHORT).show()
        }
    }
}

/**
 * Permission dialog, Settings page খোলা, Toast, আর screen-এ ফিরে আসলে status refresh,
 * এই সব Android-নির্ভর কাজ এখানে হয়। Onboarding ও Permission Center দুই screen-ই এটা ব্যবহার করে।
 */
@Composable
fun PermissionEffectsHandler(viewModel: PermissionViewModel) {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val activity = context.findActivity()
        val permanent: Set<String> = if (activity == null) {
            emptySet()
        } else {
            result.filterValues { granted -> !granted }
                .keys
                .filter { name -> !ActivityCompat.shouldShowRequestPermissionRationale(activity, name) }
                .toSet()
        }
        viewModel.onEvent(PermissionEvent.RuntimeResult(result, permanent))
    }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.onEvent(PermissionEvent.ScreenResumed)
    }

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is PermissionEffect.RequestRuntime -> launcher.launch(effect.permissions.toTypedArray())
                is PermissionEffect.OpenSettings -> {
                    context.launchSettings(PermissionIntents.settingsFor(context, effect.id))
                    viewModel.onEvent(PermissionEvent.SettingsLaunched)
                }
                PermissionEffect.OpenAppDetails -> context.launchSettings(PermissionIntents.appDetails(context))
                is PermissionEffect.ShowMessage ->
                    Toast.makeText(context, effect.message, Toast.LENGTH_LONG).show()
            }
        }
    }
}
