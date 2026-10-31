package com.aiagent.assistant.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.aiagent.assistant.R
import com.aiagent.assistant.presentation.agent.BackgroundAgentScreen
import com.aiagent.assistant.presentation.ai.AiModelScreen
import com.aiagent.assistant.presentation.ai.ProviderEditorScreen
import com.aiagent.assistant.presentation.chat.ChatScreen
import com.aiagent.assistant.presentation.components.PlaceholderScreen
import com.aiagent.assistant.presentation.designsystem.DesignSystemScreen
import com.aiagent.assistant.presentation.history.HistoryScreen
import com.aiagent.assistant.presentation.home.HomeScreen
import com.aiagent.assistant.presentation.lab.ScreenLabScreen
import com.aiagent.assistant.presentation.permission.OnboardingPermissionScreen
import com.aiagent.assistant.presentation.permission.PermissionCenterScreen
import com.aiagent.assistant.presentation.settings.AboutScreen
import com.aiagent.assistant.presentation.settings.AgentBehaviorScreen
import com.aiagent.assistant.presentation.settings.AppearanceScreen
import com.aiagent.assistant.presentation.settings.AssistantSettingsScreen
import com.aiagent.assistant.presentation.settings.LanguageScreen
import com.aiagent.assistant.presentation.settings.PrivacyScreen
import com.aiagent.assistant.presentation.settings.ProfileScreen
import com.aiagent.assistant.presentation.settings.SettingsScreen
import com.aiagent.assistant.presentation.settings.VoiceSettingsScreen
import com.aiagent.assistant.presentation.skills.SkillDetailScreen
import com.aiagent.assistant.presentation.skills.SkillEditorScreen
import com.aiagent.assistant.presentation.skills.SkillsScreen
import com.aiagent.assistant.presentation.voice.VoiceScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String,
    onOpenDrawer: () -> Unit,
    onOnboardingFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val goBack: () -> Unit = { navController.popBackStack() }
    val goTo: (String) -> Unit = { route -> navController.navigateTo(route) }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Routes.ONBOARDING) {
            OnboardingPermissionScreen(
                onFinished = {
                    onOnboardingFinished()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }

        // ───── নিচের বারের ৫ ট্যাব ─────
        composable(Screen.Home.route) {
            HomeScreen(onOpenDrawer = onOpenDrawer, onNavigate = goTo)
        }
        composable(Screen.Chat.route) {
            ChatScreen(onNavigate = goTo, onOpenDrawer = onOpenDrawer)
        }
        composable(
            route = Routes.CHAT_DETAIL,
            arguments = listOf(navArgument("chatId") { type = NavType.LongType })
        ) {
            ChatScreen(onNavigate = goTo, onBack = goBack)
        }
        composable(
            route = Routes.VOICE,
            arguments = listOf(
                navArgument("chatId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) {
            VoiceScreen(
                onClose = goBack,
                onOpenChat = { id ->
                    navController.navigate(Routes.chat(id)) {
                        popUpTo(Routes.VOICE) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.ImageGen.route) {
            PlaceholderScreen(
                title = stringResource(R.string.tile_image_title),
                stepLabel = "Image Generator · আসছে"
            )
        }
        composable(Screen.Agents.route) {
            PlaceholderScreen(
                title = stringResource(R.string.tile_agents_title),
                stepLabel = "Step 13 · Task Planner"
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(onNavigate = goTo)
        }

        // ───── drawer থেকে খোলা পেজ ─────
        composable(Routes.HISTORY) {
            HistoryScreen(
                onOpenChat = { id -> navController.navigate(Routes.chat(id)) },
                onNewChat = { navController.navigate(Routes.chat(-1L)) }
            )
        }
        composable(Routes.FILES) {
            PlaceholderScreen(
                title = stringResource(R.string.tile_files_title),
                stepLabel = "File input · পরে আসবে"
            )
        }
        composable(Routes.AUTOMATIONS) {
            PlaceholderScreen(
                title = stringResource(R.string.nav_automations),
                stepLabel = "Step 15 · Automations"
            )
        }

        // ───── Settings ─────
        composable(Routes.SETTINGS) {
            SettingsScreen(onNavigate = goTo)
        }
        composable(Routes.SETTINGS_ASSISTANT) {
            AssistantSettingsScreen(onBack = goBack)
        }
        composable(Routes.SETTINGS_AI) {
            AiModelScreen(
                onBack = goBack,
                onEdit = { providerId, type ->
                    navController.navigate(Routes.aiEdit(providerId, type))
                }
            )
        }
        composable(
            route = Routes.SETTINGS_AI_EDIT,
            arguments = listOf(
                navArgument("providerId") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("type") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) {
            ProviderEditorScreen(onBack = goBack)
        }
        composable(Routes.SETTINGS_BACKGROUND) {
            BackgroundAgentScreen(
                onBack = goBack,
                onOpenPermissions = { navController.navigate(Routes.PERMISSIONS) }
            )
        }
        composable(Routes.SETTINGS_SKILLS) {
            SkillsScreen(
                onBack = goBack,
                onOpenSkill = { id -> navController.navigate(Routes.skillDetail(id)) },
                onNewSkill = { navController.navigate(Routes.skillEdit()) }
            )
        }
        composable(
            route = Routes.SKILL_DETAIL,
            arguments = listOf(navArgument("skillId") { type = NavType.StringType })
        ) {
            SkillDetailScreen(
                onBack = goBack,
                onEdit = { id -> navController.navigate(Routes.skillEdit(id)) }
            )
        }
        composable(
            route = Routes.SKILL_EDIT,
            arguments = listOf(
                navArgument("skillId") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) {
            SkillEditorScreen(onBack = goBack)
        }
        composable(Routes.SETTINGS_LAB) {
            ScreenLabScreen(
                onBack = goBack,
                onOpenPermissions = { navController.navigate(Routes.PERMISSIONS) }
            )
        }
        composable(Routes.SETTINGS_VOICE) {
            VoiceSettingsScreen(onBack = goBack)
        }
        composable(Routes.SETTINGS_AGENT) {
            AgentBehaviorScreen(onBack = goBack)
        }
        composable(Routes.SETTINGS_APPEARANCE) {
            AppearanceScreen(
                onBack = goBack,
                onOpenDesignSystem = { navController.navigate(Routes.DESIGN) }
            )
        }
        composable(Routes.SETTINGS_LANGUAGE) {
            LanguageScreen(onBack = goBack)
        }
        composable(Routes.SETTINGS_PRIVACY) {
            PrivacyScreen(
                onBack = goBack,
                onOpenPermissions = { navController.navigate(Routes.PERMISSIONS) }
            )
        }
        composable(Routes.SETTINGS_ABOUT) {
            AboutScreen(onBack = goBack)
        }

        composable(Routes.PERMISSIONS) {
            PermissionCenterScreen(onBack = goBack)
        }
        composable(Routes.DESIGN) {
            DesignSystemScreen(onBack = goBack)
        }
    }
}
