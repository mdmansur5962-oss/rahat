package com.aiagent.assistant.presentation.permission

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aiagent.assistant.domain.permission.PermissionCatalog

@Composable
internal fun SectionTitle(en: String, bn: String) {
    Column(modifier = Modifier.padding(top = 8.dp)) {
        Text(
            text = en,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = bn,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun InfoCard(
    icon: ImageVector,
    titleEn: String,
    titleBn: String,
    bodyEn: String,
    bodyBn: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    val cs = MaterialTheme.colorScheme
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = cs.secondaryContainer,
            contentColor = cs.onSecondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(icon, contentDescription = null, tint = cs.onSecondaryContainer)
                Column {
                    Text(titleEn, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(titleBn, style = MaterialTheme.typography.bodyMedium)
                }
            }
            Text(bodyEn, style = MaterialTheme.typography.bodyMedium)
            Text(bodyBn, style = MaterialTheme.typography.bodyMedium)
            if (actionLabel != null && onAction != null) {
                OutlinedButton(onClick = onAction, modifier = Modifier.fillMaxWidth()) {
                    Text(actionLabel)
                }
            }
        }
    }
}

@Composable
internal fun SafetyCard() {
    InfoCard(
        icon = Icons.Rounded.Security,
        titleEn = "Safety promise",
        titleBn = "নিরাপত্তা প্রতিশ্রুতি",
        bodyEn = "The assistant never works inside banking apps, never reads or stores OTP, passwords, PINs " +
            "or card details, and never confirms a payment by itself. Sensitive actions (call, SMS send, " +
            "delete) always ask for your confirmation first.",
        bodyBn = "Assistant কখনো ব্যাংকিং অ্যাপে কাজ করবে না, OTP, password, PIN বা card-এর তথ্য পড়বে বা " +
            "রাখবে না, আর নিজে থেকে payment confirm করবে না। কল, SMS পাঠানো, delete-এর মতো স্পর্শকাতর " +
            "কাজের আগে সবসময় আপনার confirmation চাইবে।"
    )
}

@Composable
internal fun RestrictedSettingsCard(onOpenAppInfo: () -> Unit) {
    InfoCard(
        icon = Icons.Rounded.Info,
        titleEn = "Switch is greyed out?",
        titleBn = "সুইচ ধূসর (grey) দেখাচ্ছে?",
        bodyEn = "If Accessibility or Notification Access is greyed out or says \"Restricted setting\" " +
            "(Android 13+, apps installed from an APK file): first try to turn it on once, then open App info, " +
            "tap the three dots (top right), choose \"Allow restricted settings\", and try again.",
        bodyBn = "Accessibility বা Notification Access ধূসর দেখালে বা \"Restricted setting\" লেখা এলে " +
            "(Android 13+, APK ফাইল থেকে install করা অ্যাপে): আগে একবার চালু করার চেষ্টা করুন, তারপর App info খুলে " +
            "উপরে ডানের তিন ডটে চাপুন, \"Allow restricted settings\" বেছে নিন, তারপর আবার চেষ্টা করুন।",
        actionLabel = "Open App Info · অ্যাপ তথ্য খুলুন",
        onAction = onOpenAppInfo
    )
}

@Composable
internal fun ProgressCard(state: PermissionUiState) {
    val cs = MaterialTheme.colorScheme
    val total = state.required.size
    val done = state.requiredGranted
    val fraction = if (total == 0) 0f else done.toFloat() / total

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = cs.surface, contentColor = cs.onSurface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Required permissions · প্রয়োজনীয় permission",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "$done / $total",
                style = MaterialTheme.typography.headlineMedium,
                color = if (state.allRequiredGranted) SuccessGreen else cs.primary
            )
            LinearProgressIndicator(
                progress = { fraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(50)),
                color = if (state.allRequiredGranted) SuccessGreen else cs.primary,
                trackColor = cs.surfaceVariant
            )
            if (state.allRequiredGranted) {
                Text(
                    text = "✓ সব প্রয়োজনীয় permission চালু · All required permissions are enabled",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SuccessGreen
                )
            }
        }
    }
}

@Composable
internal fun GuidedButtons(
    state: PermissionUiState,
    onStartRequired: () -> Unit,
    onStartEverything: () -> Unit,
    onStop: () -> Unit,
    showEverythingButton: Boolean = true
) {
    val cs = MaterialTheme.colorScheme
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (state.guidedActive) {
            val name = state.guidedCurrent?.let { PermissionCatalog.byId(it).nameEn } ?: "..."
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = cs.primaryContainer,
                    contentColor = cs.onPrimaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "ধাপ ${state.guidedIndex}/${state.guidedTotal} · Step ${state.guidedIndex}/${state.guidedTotal}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$name — Android-এর পেজে চালু করে Back চাপুন · turn it on, then press Back",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    TextButton(onClick = onStop) { Text("Stop · থামান") }
                }
            }
        }
        Button(
            onClick = onStartRequired,
            enabled = !state.guidedActive,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                text = "Enable All Required Permissions\nসব প্রয়োজনীয় permission চালু করুন",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelLarge
            )
        }
        if (showEverythingButton) {
            OutlinedButton(
                onClick = onStartEverything,
                enabled = !state.guidedActive,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = "Enable Everything (required + optional)\nসব permission চালু করুন",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
internal fun CustomPermissionsSection(
    items: List<CustomPermissionState>,
    onAdd: (String) -> Unit,
    onEnable: (String) -> Unit,
    onRemove: (String) -> Unit
) {
    val cs = MaterialTheme.colorScheme
    var text by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = cs.surface, contentColor = cs.onSurface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Add a custom permission · নতুন permission যোগ করুন",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Type a permission name that is already declared in AndroidManifest.xml. " +
                    "Android only grants permissions the manifest declares.",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "AndroidManifest.xml-এ declare করা আছে এমন permission-এর নাম লিখুন। " +
                    "Manifest-এ declare না থাকলে Android সেটা দেবে না।",
                style = MaterialTheme.typography.bodyMedium,
                color = cs.onSurfaceVariant
            )
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("android.permission.READ_CALL_LOG") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = { onAdd(text) },
                enabled = text.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Rounded.Add, null, Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Add · যোগ করুন")
            }

            items.forEach { custom ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(custom.name, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            text = if (custom.granted) "Granted · অনুমোদিত" else "Not granted · দেওয়া হয়নি",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (custom.granted) SuccessGreen else cs.error
                        )
                    }
                    if (!custom.granted) {
                        OutlinedButton(onClick = { onEnable(custom.name) }) { Text("Enable") }
                    }
                    IconButton(onClick = { onRemove(custom.name) }) {
                        Icon(Icons.Rounded.Delete, contentDescription = "Remove")
                    }
                }
            }
        }
    }
}
