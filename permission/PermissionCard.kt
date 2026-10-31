package com.aiagent.assistant.presentation.permission

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aiagent.assistant.domain.permission.PermissionKind
import com.aiagent.assistant.domain.permission.PermissionStatus
import com.aiagent.assistant.presentation.theme.agentAccentBrush

internal val SuccessGreen = Color(0xFF2EBD85)

@Composable
fun PermissionCard(
    item: PermissionItemState,
    onEnable: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val def = item.def
    val cs = MaterialTheme.colorScheme
    val shape = MaterialTheme.shapes.large
    val borderModifier = if (def.highlighted) {
        Modifier.border(2.dp, agentAccentBrush(), shape)
    } else {
        Modifier.border(1.dp, cs.outline.copy(alpha = 0.3f), shape)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(borderModifier),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = cs.surface, contentColor = cs.onSurface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(cs.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = iconFor(def.id),
                        contentDescription = null,
                        tint = cs.onPrimaryContainer
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = def.nameEn,
                        style = MaterialTheme.typography.titleMedium,
                        color = cs.onSurface
                    )
                    Text(
                        text = def.nameBn,
                        style = MaterialTheme.typography.bodyMedium,
                        color = cs.onSurfaceVariant
                    )
                }
                StatusChip(item.status)
            }

            if (def.highlighted) {
                Text(
                    text = "★ Special permission, needed for full agent mode · বিশেষ permission, full agent mode-এর জন্য জরুরি",
                    style = MaterialTheme.typography.labelMedium,
                    color = cs.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Text(
                text = def.whyEn,
                style = MaterialTheme.typography.bodyMedium,
                color = cs.onSurface
            )
            Text(
                text = def.whyBn,
                style = MaterialTheme.typography.bodyMedium,
                color = cs.onSurfaceVariant
            )

            val actionable = def.kind == PermissionKind.RUNTIME || def.kind == PermissionKind.SPECIAL
            if (item.status == PermissionStatus.NOT_SUPPORTED) {
                Text(
                    text = "এই ডিভাইসে সাপোর্ট নেই · Not supported on this device",
                    style = MaterialTheme.typography.labelMedium,
                    color = cs.onSurfaceVariant
                )
            } else if (actionable) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (!item.status.isSettled) {
                        if (item.attempted) {
                            OutlinedButton(onClick = onEnable, modifier = Modifier.weight(1f)) {
                                Icon(Icons.Rounded.Refresh, null, Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Retry · আবার", maxLines = 1)
                            }
                        } else {
                            Button(onClick = onEnable, modifier = Modifier.weight(1f)) {
                                Text("Enable · চালু করুন", maxLines = 1)
                            }
                        }
                    }
                    OutlinedButton(onClick = onOpenSettings, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Rounded.Settings, null, Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Settings · সেটিংস", maxLines = 1)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusChip(status: PermissionStatus) {
    val cs = MaterialTheme.colorScheme
    val (en, bn, color) = when (status) {
        PermissionStatus.GRANTED -> Triple("Granted", "অনুমোদিত", SuccessGreen)
        PermissionStatus.DENIED -> Triple("Not granted", "দেওয়া হয়নি", cs.error)
        PermissionStatus.ON_DEMAND -> Triple("On demand", "প্রয়োজনে", cs.secondary)
        PermissionStatus.NOT_NEEDED -> Triple("Not needed", "লাগবে না", cs.secondary)
        PermissionStatus.NOT_SUPPORTED -> Triple("Unsupported", "সাপোর্ট নেই", cs.onSurfaceVariant)
    }
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.16f))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = en,
            style = MaterialTheme.typography.labelMedium,
            color = color,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
        Text(
            text = bn,
            style = MaterialTheme.typography.labelMedium,
            color = color,
            maxLines = 1
        )
    }
}
