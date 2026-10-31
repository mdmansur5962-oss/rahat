package com.aiagent.assistant.presentation.permission

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Accessibility
import androidx.compose.material.icons.rounded.Alarm
import androidx.compose.material.icons.rounded.BatterySaver
import androidx.compose.material.icons.rounded.Bluetooth
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.ContentPaste
import androidx.compose.material.icons.rounded.Contacts
import androidx.compose.material.icons.rounded.DoNotDisturbOn
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.InstallMobile
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material.icons.rounded.Nfc
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.QueryStats
import androidx.compose.material.icons.rounded.RecordVoiceOver
import androidx.compose.material.icons.rounded.ScreenShare
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Sms
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.ui.graphics.vector.ImageVector
import com.aiagent.assistant.domain.permission.PermissionId

internal fun iconFor(id: PermissionId): ImageVector = when (id) {
    PermissionId.ACCESSIBILITY -> Icons.Rounded.Accessibility
    PermissionId.OVERLAY -> Icons.Rounded.Layers
    PermissionId.NOTIFICATION_LISTENER -> Icons.Rounded.Notifications
    PermissionId.MICROPHONE -> Icons.Rounded.Mic
    PermissionId.SPEECH_RECOGNITION -> Icons.Rounded.RecordVoiceOver
    PermissionId.STORAGE -> Icons.Rounded.Folder
    PermissionId.CAMERA -> Icons.Rounded.CameraAlt
    PermissionId.CONTACTS -> Icons.Rounded.Contacts
    PermissionId.PHONE -> Icons.Rounded.Call
    PermissionId.SMS -> Icons.Rounded.Sms
    PermissionId.CALENDAR -> Icons.Rounded.CalendarMonth
    PermissionId.ALARM_REMINDER -> Icons.Rounded.Alarm
    PermissionId.LOCATION -> Icons.Rounded.LocationOn
    PermissionId.BACKGROUND_LOCATION -> Icons.Rounded.MyLocation
    PermissionId.BLUETOOTH -> Icons.Rounded.Bluetooth
    PermissionId.WIFI_NETWORK -> Icons.Rounded.Wifi
    PermissionId.MEDIA_CONTROL -> Icons.Rounded.PlayCircle
    PermissionId.SCREEN_CAPTURE -> Icons.Rounded.ScreenShare
    PermissionId.CLIPBOARD -> Icons.Rounded.ContentPaste
    PermissionId.APP_USAGE -> Icons.Rounded.QueryStats
    PermissionId.BATTERY_OPTIMIZATION -> Icons.Rounded.BatterySaver
    PermissionId.BACKGROUND_SERVICE -> Icons.Rounded.Sync
    PermissionId.INSTALL_UNKNOWN_APPS -> Icons.Rounded.InstallMobile
    PermissionId.FILE_SHARING -> Icons.Rounded.Share
    PermissionId.NFC -> Icons.Rounded.Nfc
    PermissionId.BIOMETRIC -> Icons.Rounded.Fingerprint
    PermissionId.VIBRATE -> Icons.Rounded.Vibration
    PermissionId.WAKE_LOCK -> Icons.Rounded.Lightbulb
    PermissionId.WRITE_SETTINGS -> Icons.Rounded.Settings
    PermissionId.DND_ACCESS -> Icons.Rounded.DoNotDisturbOn
}

internal val FallbackPermissionIcon: ImageVector = Icons.Rounded.Security
