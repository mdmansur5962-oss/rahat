package com.aiagent.assistant.presentation.drawer

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.ChatBubble
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.SmartToy
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aiagent.assistant.BuildConfig
import com.aiagent.assistant.R
import com.aiagent.assistant.presentation.components.BrandLogo
import com.aiagent.assistant.presentation.components.OnlineBadge
import com.aiagent.assistant.presentation.components.brandLetter
import com.aiagent.assistant.presentation.navigation.Routes
import com.aiagent.assistant.presentation.navigation.Screen

private data class DrawerEntry(
    val route: String,
    @StringRes val labelRes: Int,
    val icon: ImageVector
)

private val mainEntries = listOf(
    DrawerEntry(Screen.Home.route, R.string.nav_home, Icons.Rounded.Home),
    DrawerEntry(Screen.Chat.route, R.string.nav_chat, Icons.Rounded.ChatBubble),
    DrawerEntry(Screen.ImageGen.route, R.string.tile_image_title, Icons.Rounded.Image),
    DrawerEntry(Screen.Agents.route, R.string.tile_agents_title, Icons.Rounded.SmartToy),
    DrawerEntry(Routes.HISTORY, R.string.tile_history_title, Icons.Rounded.History),
    DrawerEntry(Routes.AUTOMATIONS, R.string.nav_automations, Icons.Rounded.Bolt)
)

private val toolEntries = listOf(
    DrawerEntry(Routes.SETTINGS, R.string.nav_settings, Icons.Rounded.Settings),
    DrawerEntry(Routes.PERMISSIONS, R.string.drawer_permissions, Icons.Rounded.Security),
    DrawerEntry(Routes.DESIGN, R.string.drawer_design, Icons.Rounded.Palette)
)

/** বাঁ পাশের drawer: লোগো, নাম, সব পেজের তালিকা, নিচে version */
@Composable
fun AppDrawerContent(
    currentRoute: String?,
    userName: String,
    appName: String,
    onNavigate: (String) -> Unit
) {
    val cs = MaterialTheme.colorScheme

    ModalDrawerSheet(
        drawerContainerColor = cs.surfaceContainer,
        drawerContentColor = cs.onSurface
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                BrandLogo(size = 56.dp, letter = appName.brandLetter())
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = appName,
                        style = MaterialTheme.typography.headlineSmall,
                        color = cs.onSurface
                    )
                    Text(
                        text = stringResource(R.string.drawer_tagline),
                        style = MaterialTheme.typography.bodySmall,
                        color = cs.onSurfaceVariant
                    )
                    OnlineBadge()
                }
            }
            if (userName.isNotBlank()) {
                Text(
                    text = "👋 $userName",
                    style = MaterialTheme.typography.titleMedium,
                    color = cs.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                )
            }
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp, horizontal = 20.dp),
                color = cs.outline.copy(alpha = 0.3f)
            )

            mainEntries.forEach { entry ->
                DrawerItem(entry = entry, selected = currentRoute == entry.route, onClick = { onNavigate(entry.route) })
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp, horizontal = 20.dp),
                color = cs.outline.copy(alpha = 0.3f)
            )

            toolEntries.forEach { entry ->
                DrawerItem(entry = entry, selected = currentRoute == entry.route, onClick = { onNavigate(entry.route) })
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "$appName v${BuildConfig.VERSION_NAME}",
                style = MaterialTheme.typography.labelSmall,
                color = cs.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }
    }
}

@Composable
private fun DrawerItem(
    entry: DrawerEntry,
    selected: Boolean,
    onClick: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    NavigationDrawerItem(
        label = { Text(stringResource(entry.labelRes)) },
        selected = selected,
        onClick = onClick,
        icon = { Icon(entry.icon, contentDescription = null) },
        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = cs.primary.copy(alpha = 0.20f),
            unselectedContainerColor = Color.Transparent,
            selectedIconColor = cs.onPrimaryContainer,
            unselectedIconColor = cs.onSurfaceVariant,
            selectedTextColor = cs.onPrimaryContainer,
            unselectedTextColor = cs.onSurface
        )
    )
}
