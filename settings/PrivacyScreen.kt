package com.aiagent.assistant.presentation.settings

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiagent.assistant.domain.chat.ChatRepository
import com.aiagent.assistant.presentation.components.ConfirmActionDialog
import com.aiagent.assistant.presentation.components.GlassCard
import com.aiagent.assistant.presentation.components.SettingsGroup
import com.aiagent.assistant.presentation.components.SettingsRow
import com.aiagent.assistant.presentation.permission.SafetyCard
import com.aiagent.assistant.presentation.theme.IconTone
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class PrivacyViewModel @Inject constructor(
    private val repository: ChatRepository
) : ViewModel() {
    fun deleteAllChats() {
        viewModelScope.launch { repository.deleteAll() }
    }
}

@Composable
fun PrivacyScreen(
    onBack: () -> Unit,
    onOpenPermissions: () -> Unit,
    viewModel: PrivacyViewModel = hiltViewModel()
) {
    val cs = MaterialTheme.colorScheme
    val context = LocalContext.current
    var confirmDeleteAll by remember { mutableStateOf(false) }

    SettingsPage(
        title = "Privacy & Security · গোপনীয়তা",
        subtitle = "ডেটা, permission, নিরাপত্তা",
        onBack = onBack
    ) {
        SafetyCard()

        SettingsGroup {
            SettingsRow(
                icon = Icons.Rounded.Security,
                tone = IconTone.RED,
                title = "Permission Center · পারমিশন সেন্টার",
                subtitle = "কোন permission দেওয়া আছে দেখুন ও বদলান",
                onClick = onOpenPermissions
            )
            SettingsRow(
                icon = Icons.Rounded.Apps,
                tone = IconTone.SLATE,
                title = "App info · অ্যাপ তথ্য",
                subtitle = "Android-এর সেটিংসে অ্যাপের পেজ",
                onClick = {
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:${context.packageName}")
                    )
                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        // কিছু করার নেই
                    }
                }
            )
            SettingsRow(
                icon = Icons.Rounded.DeleteForever,
                tone = IconTone.ORANGE,
                title = "Delete all chats · সব চ্যাট মুছুন",
                subtitle = "এই ফোনে জমানো সব কথোপকথন",
                onClick = { confirmDeleteAll = true },
                showDivider = false
            )
        }

        GlassCard {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Data & privacy · ডেটা ও গোপনীয়তা",
                    style = MaterialTheme.typography.titleMedium,
                    color = cs.onSurface
                )
                Text(
                    text = "আপনার settings ও চ্যাট শুধু এই ফোনে সংরক্ষিত থাকে। চ্যাটের বার্তা কেবল আপনার বেছে নেওয়া AI provider-এর কাছে " +
                        "যায়, আপনার নিজের API key দিয়ে। API key ফোনে এনক্রিপ্ট করে রাখা হয়।",
                    style = MaterialTheme.typography.bodySmall,
                    color = cs.onSurfaceVariant
                )
                Text(
                    text = "Your settings and chats stay on this phone. Chat messages go only to the AI provider " +
                        "you choose, using your own API key, which is stored encrypted on this phone.",
                    style = MaterialTheme.typography.bodySmall,
                    color = cs.onSurfaceVariant
                )
            }
        }
    }

    if (confirmDeleteAll) {
        ConfirmActionDialog(
            title = "Delete all chats? · সব চ্যাট মুছবেন?",
            message = "সব কথোপকথন চিরতরে মুছে যাবে, ফিরে পাওয়া যাবে না।",
            confirmText = "Delete all · সব মুছুন",
            destructive = true,
            onConfirm = {
                confirmDeleteAll = false
                viewModel.deleteAllChats()
                Toast.makeText(context, "Deleted · মুছে ফেলা হয়েছে", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { confirmDeleteAll = false }
        )
    }
}
