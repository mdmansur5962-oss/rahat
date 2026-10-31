package com.aiagent.assistant.presentation.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ChatBubble
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SmartToy
import androidx.compose.material.icons.rounded.UploadFile
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aiagent.assistant.presentation.components.AgentBottomBar
import com.aiagent.assistant.presentation.components.AgentChip
import com.aiagent.assistant.presentation.components.AgentDialog
import com.aiagent.assistant.presentation.components.AgentIconButton
import com.aiagent.assistant.presentation.components.AgentSwitchRow
import com.aiagent.assistant.presentation.components.AgentTextField
import com.aiagent.assistant.presentation.components.AiOrb
import com.aiagent.assistant.presentation.components.OrbState
import com.aiagent.assistant.presentation.components.RecentChatRow
import com.aiagent.assistant.presentation.components.AskBar
import com.aiagent.assistant.presentation.components.Avatar
import com.aiagent.assistant.presentation.components.BottomBarItem
import com.aiagent.assistant.presentation.components.BrandLogo
import com.aiagent.assistant.presentation.components.ConfirmActionDialog
import com.aiagent.assistant.presentation.components.FeatureTile
import com.aiagent.assistant.presentation.components.GlassButton
import com.aiagent.assistant.presentation.components.GlassCard
import com.aiagent.assistant.presentation.components.GradientBackground
import com.aiagent.assistant.presentation.components.GradientButton
import com.aiagent.assistant.presentation.components.IconTile
import com.aiagent.assistant.presentation.components.OnlineBadge
import com.aiagent.assistant.presentation.components.PromoBanner
import com.aiagent.assistant.presentation.components.ScreenHeader
import com.aiagent.assistant.presentation.components.SectionHeader
import com.aiagent.assistant.presentation.components.SegmentedPicker
import com.aiagent.assistant.presentation.components.SelectableCard
import com.aiagent.assistant.presentation.components.SettingsGroup
import com.aiagent.assistant.presentation.components.SettingsRow
import com.aiagent.assistant.presentation.components.ShimmerBox
import com.aiagent.assistant.presentation.components.StatRow
import com.aiagent.assistant.presentation.components.StatusPill
import com.aiagent.assistant.presentation.components.TypingDots
import com.aiagent.assistant.presentation.theme.AccentPalette
import com.aiagent.assistant.presentation.theme.AgentDesign
import com.aiagent.assistant.presentation.theme.AgentExtraShapes
import com.aiagent.assistant.presentation.theme.IconTone
import com.aiagent.assistant.presentation.theme.ThemeMode
import com.aiagent.assistant.presentation.theme.ThemeViewModel

/** MYRA design-এর সব উপাদান এক জায়গায়। এখান থেকে theme ও রঙ বদলালে সাথে সাথে পুরো অ্যাপে প্রয়োগ হয়। */
@Composable
fun DesignSystemScreen(
    onBack: () -> Unit,
    themeViewModel: ThemeViewModel = hiltViewModel()
) {
    val cs = MaterialTheme.colorScheme
    val themeMode by themeViewModel.themeMode.collectAsStateWithLifecycle()
    val accent by themeViewModel.accent.collectAsStateWithLifecycle()

    var fieldValue by remember { mutableStateOf("") }
    var hapticOn by remember { mutableStateOf(true) }
    var selectedModel by remember { mutableIntStateOf(0) }
    var barIndex by remember { mutableIntStateOf(0) }
    var showDialog by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }
    var showDelete by remember { mutableStateOf(false) }
    var orbIndex by remember { mutableIntStateOf(0) }

    val modes = listOf(ThemeMode.SYSTEM, ThemeMode.LIGHT, ThemeMode.DARK)

    GradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ScreenHeader(
                title = "Design System",
                subtitle = "ডিজাইন সিস্টেম · MYRA style",
                onBack = onBack
            )

            // ───── Brand ─────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BrandLogo(size = 56.dp)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "MYRA",
                        style = MaterialTheme.typography.headlineMedium,
                        color = cs.onBackground
                    )
                    Text(
                        text = "AI Assistant",
                        style = MaterialTheme.typography.bodyMedium,
                        color = cs.onSurfaceVariant
                    )
                }
                OnlineBadge()
            }

            // ───── Orb ─────
            SectionHeader(title = "AI Orb · অর্ব")
            val orbStates = listOf(OrbState.IDLE, OrbState.LISTENING, OrbState.THINKING)
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AiOrb(orbSize = 180.dp, state = orbStates[orbIndex])
                SegmentedPicker(
                    options = listOf("Idle", "Listening", "Thinking"),
                    selectedIndex = orbIndex,
                    onSelect = { orbIndex = it }
                )
            }

            // ───── Appearance ─────
            SectionHeader(title = "Appearance · চেহারা")
            GlassCard {
                Text("Theme · থিম", style = MaterialTheme.typography.titleMedium, color = cs.onSurface)
                Box(modifier = Modifier.padding(top = 8.dp)) {
                    SegmentedPicker(
                        options = listOf("System · সিস্টেম", "Light · লাইট", "Dark · ডার্ক"),
                        selectedIndex = modes.indexOf(themeMode).coerceAtLeast(0),
                        onSelect = { themeViewModel.setThemeMode(modes[it]) }
                    )
                }
                Text(
                    text = "Accent color · রঙ",
                    style = MaterialTheme.typography.titleMedium,
                    color = cs.onSurface,
                    modifier = Modifier.padding(top = 16.dp)
                )
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AccentPalette.entries.forEach { palette ->
                        val selected = palette == accent
                        val ring = if (selected) {
                            Modifier.border(3.dp, cs.onBackground, CircleShape)
                        } else {
                            Modifier
                        }
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(palette.primary, palette.secondary)))
                                .then(ring)
                                .clickable { themeViewModel.setAccent(palette) },
                            contentAlignment = Alignment.Center
                        ) {
                            if (selected) {
                                Icon(Icons.Rounded.Check, contentDescription = palette.nameEn, tint = Color.White)
                            }
                        }
                    }
                }
                Text(
                    text = "${accent.nameEn} · ${accent.nameBn}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = cs.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // ───── Home components ─────
            SectionHeader(title = "Home · হোম")
            AskBar(
                placeholder = "Ask anything...",
                onClick = {},
                onAttachClick = {},
                onMicClick = {}
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FeatureTile(
                    icon = Icons.Rounded.ChatBubble,
                    title = "Chat",
                    subtitle = "Ask anything",
                    tone = IconTone.PURPLE,
                    onClick = {},
                    modifier = Modifier.weight(1f)
                )
                FeatureTile(
                    icon = Icons.Rounded.Image,
                    title = "Image",
                    subtitle = "Create images",
                    tone = IconTone.TEAL,
                    onClick = {},
                    modifier = Modifier.weight(1f)
                )
                FeatureTile(
                    icon = Icons.Rounded.SmartToy,
                    title = "AI Agents",
                    subtitle = "Specialized bots",
                    tone = IconTone.GREEN,
                    onClick = {},
                    modifier = Modifier.weight(1f)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FeatureTile(
                    icon = Icons.Rounded.UploadFile,
                    title = "File Upload",
                    subtitle = "PDF, DOC, etc.",
                    tone = IconTone.SKY,
                    onClick = {},
                    modifier = Modifier.weight(1f)
                )
                FeatureTile(
                    icon = Icons.Rounded.History,
                    title = "History",
                    subtitle = "Your conversations",
                    tone = IconTone.PURPLE,
                    onClick = {},
                    modifier = Modifier.weight(1f)
                )
                FeatureTile(
                    icon = Icons.Rounded.GridView,
                    title = "More",
                    subtitle = "Tools & settings",
                    tone = IconTone.SLATE,
                    onClick = {},
                    modifier = Modifier.weight(1f)
                )
            }
            PromoBanner(
                title = "Create Amazing Images",
                subtitle = "Turn your ideas into stunning visuals with AI.",
                onClick = {}
            )

            // ───── Chat components ─────
            SectionHeader(title = "Chat · চ্যাট")
            GlassCard {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    BrandLogo(size = 40.dp)
                    Column(modifier = Modifier.weight(1f)) {
                        Text("MYRA", style = MaterialTheme.typography.titleMedium, color = cs.onSurface)
                        Text("AI Assistant", style = MaterialTheme.typography.bodySmall, color = cs.onSurfaceVariant)
                    }
                    OnlineBadge()
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AgentChip(text = "Explain", icon = Icons.Rounded.Lightbulb, onClick = {})
                AgentChip(text = "Write email", icon = Icons.Rounded.Email, onClick = {})
                AgentChip(text = "Solve", icon = Icons.Rounded.Psychology, onClick = {})
            }
            Box(
                modifier = Modifier
                    .clip(AgentExtraShapes.bubbleUser)
                    .background(AgentDesign.colors.brand)
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text("Chrome-এ যাও", color = Color.White, style = MaterialTheme.typography.bodyMedium)
            }
            Box(
                modifier = Modifier
                    .clip(AgentExtraShapes.bubbleAssistant)
                    .background(cs.surfaceContainerHigh)
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text("ঠিক আছে, শুরু করছি।", color = cs.onSurface, style = MaterialTheme.typography.bodyMedium)
            }
            Box(
                modifier = Modifier
                    .clip(AgentExtraShapes.bubbleAssistant)
                    .background(cs.surfaceContainerHigh)
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                TypingDots()
            }

            // ───── Recent chats ─────
            SectionHeader(title = "Recent chats · সাম্প্রতিক চ্যাট")
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                RecentChatRow(
                    title = "Write a cover letter",
                    preview = "Sure, here is a draft you can edit...",
                    time = "10:24",
                    onClick = {}
                )
                RecentChatRow(
                    title = "আজকের আবহাওয়া",
                    preview = "আজ আকাশ পরিষ্কার থাকবে।",
                    time = "Yesterday",
                    onClick = {}
                )
            }

            // ───── Settings components ─────
            SectionHeader(title = "Settings · সেটিংস")
            GlassCard {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Avatar()
                    Column {
                        Text("Your Name", style = MaterialTheme.typography.titleMedium, color = cs.onSurface)
                        Text("you@example.com", style = MaterialTheme.typography.bodySmall, color = cs.onSurfaceVariant)
                    }
                }
            }
            SettingsGroup {
                SettingsRow(
                    icon = Icons.Rounded.Psychology,
                    tone = IconTone.PURPLE,
                    title = "AI Model",
                    subtitle = "Choose your preferred model",
                    onClick = {}
                )
                SettingsRow(
                    icon = Icons.Rounded.Palette,
                    tone = IconTone.BLUE,
                    title = "Appearance",
                    subtitle = "Dark mode, theme, font size",
                    onClick = {}
                )
                SettingsRow(
                    icon = Icons.Rounded.Notifications,
                    tone = IconTone.ORANGE,
                    title = "Notifications",
                    subtitle = "Chat, updates, alerts",
                    onClick = {}
                )
                SettingsRow(
                    icon = Icons.Rounded.Language,
                    tone = IconTone.GREEN,
                    title = "Language",
                    subtitle = "English / বাংলা",
                    onClick = {}
                )
                SettingsRow(
                    icon = Icons.Rounded.Lock,
                    tone = IconTone.RED,
                    title = "Privacy & Security",
                    subtitle = "Data, permissions, encryption",
                    onClick = {}
                )
                SettingsRow(
                    icon = Icons.Rounded.Info,
                    tone = IconTone.SLATE,
                    title = "About",
                    subtitle = "App version, help & support",
                    onClick = {},
                    showDivider = false
                )
            }
            StatRow(
                stats = listOf(
                    "12" to "Conversations",
                    "48" to "Images Created",
                    "3" to "AI Agents"
                )
            )

            // ───── AI model picker ─────
            SectionHeader(title = "Choose a model · মডেল বাছুন")
            val models = listOf(
                Triple("GPT-4o", "Most capable, fast and smart", "(Recommended)"),
                Triple("Claude", "Great for writing and analysis", null),
                Triple("Llama 3", "Open source and versatile", null)
            )
            val tones = listOf(IconTone.GREEN, IconTone.ORANGE, IconTone.BLUE)
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                models.forEachIndexed { index, (name, desc, tag) ->
                    SelectableCard(
                        title = name,
                        subtitle = desc,
                        tag = tag,
                        selected = index == selectedModel,
                        onClick = { selectedModel = index },
                        leading = {
                            IconTile(icon = Icons.Rounded.AutoAwesome, tone = tones[index], size = 44.dp)
                        }
                    )
                }
            }

            // ───── Buttons ─────
            SectionHeader(title = "Buttons · বাটন")
            GlassCard {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    GradientButton(
                        text = "Generate Image · ছবি বানান",
                        onClick = { showDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        icon = Icons.Rounded.AutoAwesome
                    )
                    GradientButton(
                        text = "With subtitle",
                        subtitle = "সাবটাইটেল সহ",
                        onClick = {},
                        modifier = Modifier.fillMaxWidth()
                    )
                    GlassButton(
                        text = "Glass · গ্লাস বাটন",
                        onClick = { showConfirm = true },
                        modifier = Modifier.fillMaxWidth()
                    )
                    GradientButton(
                        text = "Destructive · মুছুন",
                        onClick = { showDelete = true },
                        modifier = Modifier.fillMaxWidth(),
                        destructive = true
                    )
                    GradientButton(
                        text = "Loading",
                        onClick = {},
                        modifier = Modifier.fillMaxWidth(),
                        loading = true
                    )
                    GradientButton(
                        text = "Disabled · বন্ধ",
                        onClick = {},
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        AgentIconButton(
                            icon = Icons.Rounded.Mic,
                            contentDescription = "Voice",
                            onClick = {},
                            filled = true
                        )
                        AgentIconButton(
                            icon = Icons.Rounded.Search,
                            contentDescription = "Search",
                            onClick = {}
                        )
                    }
                }
            }

            // ───── Inputs & misc ─────
            SectionHeader(title = "Inputs & more · ইনপুট ও অন্যান্য")
            GlassCard {
                AgentTextField(
                    value = fieldValue,
                    onValueChange = { fieldValue = it },
                    label = "Message · বার্তা",
                    placeholder = "৩ অক্ষরের কম লিখলে error দেখাবে",
                    leadingIcon = Icons.Rounded.Search,
                    isError = fieldValue.isNotEmpty() && fieldValue.length < 3,
                    supportingText = if (fieldValue.isNotEmpty() && fieldValue.length < 3) {
                        "কমপক্ষে ৩ অক্ষর · At least 3 characters"
                    } else {
                        null
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Box(modifier = Modifier.padding(top = 12.dp)) {
                    AgentSwitchRow(
                        title = "Haptic feedback · কম্পন",
                        subtitle = "বাটন চাপলে হালকা কম্পন",
                        checked = hapticOn,
                        onCheckedChange = { hapticOn = it }
                    )
                }
                Row(
                    modifier = Modifier.padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatusPill("Granted", AgentDesign.colors.success)
                    StatusPill("Warning", AgentDesign.colors.warning)
                    StatusPill("Error", cs.error)
                }
            }
            GlassCard(highlighted = true) {
                Text(
                    "Highlighted card · হাইলাইট কার্ড",
                    style = MaterialTheme.typography.titleMedium,
                    color = cs.onSurface
                )
                Text(
                    "Gradient border দিয়ে বিশেষ জিনিস আলাদা করা হয়।",
                    style = MaterialTheme.typography.bodyMedium,
                    color = cs.onSurfaceVariant
                )
            }

            // ───── Loading ─────
            SectionHeader(title = "Loading · লোড হচ্ছে")
            GlassCard {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    ShimmerBox(modifier = Modifier.fillMaxWidth().height(18.dp))
                    ShimmerBox(modifier = Modifier.fillMaxWidth(0.7f).height(18.dp))
                    ShimmerBox(modifier = Modifier.fillMaxWidth().height(60.dp))
                }
            }

            // ───── Colors & typography ─────
            SectionHeader(title = "Colors · রঙ")
            GlassCard {
                val swatches = listOf(
                    "Primary" to cs.primary,
                    "Secondary" to cs.secondary,
                    "Tertiary" to cs.tertiary,
                    "Surface" to cs.surface,
                    "Error" to cs.error,
                    "Success" to AgentDesign.colors.success
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    swatches.forEach { (name, color) ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(color)
                                    .border(1.dp, cs.outline.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            )
                            Text(
                                text = name,
                                style = MaterialTheme.typography.labelSmall,
                                color = cs.onSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp),
                                maxLines = 1
                            )
                        }
                    }
                }
            }
            SectionHeader(title = "Typography · লেখার ধরন")
            GlassCard {
                Text("Headline · শিরোনাম", style = MaterialTheme.typography.headlineMedium, color = cs.onSurface)
                Text("Title · উপশিরোনাম", style = MaterialTheme.typography.titleLarge, color = cs.onSurface)
                Text(
                    "Body: আমি আপনার AI সহকারী। কী করতে পারি বলুন।",
                    style = MaterialTheme.typography.bodyLarge,
                    color = cs.onSurface
                )
                Text(
                    "Label · ছোট লেখা",
                    style = MaterialTheme.typography.labelMedium,
                    color = cs.onSurfaceVariant
                )
            }

            // ───── Bottom bar ─────
            SectionHeader(title = "Bottom bar · নিচের বার")
            Box(modifier = Modifier.clip(MaterialTheme.shapes.medium)) {
                AgentBottomBar(
                    items = listOf(
                        BottomBarItem("Home", Icons.Rounded.Home),
                        BottomBarItem("Chat", Icons.Rounded.ChatBubble),
                        BottomBarItem("Image", Icons.Rounded.Image),
                        BottomBarItem("Agents", Icons.Rounded.SmartToy),
                        BottomBarItem("Profile", Icons.Rounded.Person)
                    ),
                    selectedIndex = barIndex,
                    onSelect = { barIndex = it }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Step 3 · MYRA Design System",
                style = MaterialTheme.typography.labelMedium,
                color = cs.onSurfaceVariant,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    if (showDialog) {
        AgentDialog(
            title = "Dialog · ডায়ালগ",
            subtitle = "এটা সাধারণ ডায়ালগ। নিজের content-ও রাখা যায়।",
            icon = Icons.Rounded.AutoAwesome,
            onDismiss = { showDialog = false },
            confirmText = "OK",
            onConfirm = { showDialog = false },
            dismissText = "Cancel · বাতিল"
        )
    }
    if (showConfirm) {
        ConfirmActionDialog(
            title = "Call Rahim? · রহিমকে কল করবেন?",
            message = "কল, SMS পাঠানো বা delete-এর আগে Assistant এভাবে আপনার confirmation চাইবে।",
            onConfirm = { showConfirm = false },
            onDismiss = { showConfirm = false }
        )
    }
    if (showDelete) {
        ConfirmActionDialog(
            title = "Delete this chat? · চ্যাট মুছবেন?",
            message = "মুছে ফেললে আর ফিরে পাওয়া যাবে না।",
            confirmText = "Delete · মুছুন",
            destructive = true,
            onConfirm = { showDelete = false },
            onDismiss = { showDelete = false }
        )
    }
}
