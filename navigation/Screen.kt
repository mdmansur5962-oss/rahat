package com.aiagent.assistant.presentation.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChatBubble
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.SmartToy
import androidx.compose.ui.graphics.vector.ImageVector
import com.aiagent.assistant.R

/** নিচের বারে নেই এমন route (drawer বা অন্য জায়গা থেকে খোলে) */
object Routes {
    const val ONBOARDING = "onboarding"
    const val PERMISSIONS = "permissions"
    const val DESIGN = "design"
    const val SETTINGS = "settings"
    const val AUTOMATIONS = "automations"
    const val HISTORY = "history"
    const val FILES = "files"
    const val CHAT_DETAIL = "chat/{chatId}"
    const val VOICE = "voice?chatId={chatId}"

    /** আগে থেকে থাকা চ্যাট খোলে; id <= 0 হলে ফাঁকা নতুন চ্যাট */
    fun chat(chatId: Long): String = "chat/$chatId"

    fun skillDetail(skillId: String): String = "settings/skills/detail/$skillId"

    /** skillId দিলে সেই custom skill সম্পাদনা; না দিলে নতুন skill */
    fun skillEdit(skillId: String? = null): String = "settings/skills/edit?skillId=${skillId.orEmpty()}"

    /** Live Voice খোলে; chatId দিলে সেই চ্যাটেই কথোপকথন চলে, না দিলে নতুন চ্যাট */
    fun voice(chatId: Long? = null): String = "voice?chatId=${chatId ?: -1L}"

    /** providerId দিলে সেই provider সম্পাদনা; না দিলে type দিয়ে নতুন provider */
    fun aiEdit(providerId: String? = null, type: String? = null): String =
        "settings/ai/edit?providerId=${providerId.orEmpty()}&type=${type.orEmpty()}"
    const val SETTINGS_ASSISTANT = "settings/assistant"
    const val SETTINGS_AI = "settings/ai"
    const val SETTINGS_BACKGROUND = "settings/background"
    const val SETTINGS_LAB = "settings/lab"
    const val SETTINGS_SKILLS = "settings/skills"
    const val SKILL_DETAIL = "settings/skills/detail/{skillId}"
    const val SKILL_EDIT = "settings/skills/edit?skillId={skillId}"
    const val SETTINGS_AI_EDIT = "settings/ai/edit?providerId={providerId}&type={type}"
    const val SETTINGS_VOICE = "settings/voice"
    const val SETTINGS_AGENT = "settings/agent"
    const val SETTINGS_APPEARANCE = "settings/appearance"
    const val SETTINGS_LANGUAGE = "settings/language"
    const val SETTINGS_PRIVACY = "settings/privacy"
    const val SETTINGS_ABOUT = "settings/about"
}

/** নিচের বারের ৫টি ট্যাব (MYRA design অনুযায়ী) */
sealed class Screen(
    val route: String,
    @StringRes val titleRes: Int,
    val icon: ImageVector
) {
    data object Home : Screen("home", R.string.nav_home, Icons.Rounded.Home)
    data object Chat : Screen("chat", R.string.nav_chat, Icons.Rounded.ChatBubble)
    data object ImageGen : Screen("image", R.string.nav_image, Icons.Rounded.Image)
    data object Agents : Screen("agents", R.string.nav_agents, Icons.Rounded.SmartToy)
    data object Profile : Screen("profile", R.string.nav_profile, Icons.Rounded.Person)

    companion object {
        val bottomNavItems: List<Screen> = listOf(Home, Chat, ImageGen, Agents, Profile)

        /** নিচের বার ও drawer-এর swipe যে screen-গুলোতে থাকবে */
        val chromeRoutes: Set<String> = bottomNavItems.map { it.route }.toSet() + setOf(
            Routes.SETTINGS,
            Routes.AUTOMATIONS,
            Routes.HISTORY,
            Routes.FILES,
            Routes.CHAT_DETAIL
        )
    }
}
