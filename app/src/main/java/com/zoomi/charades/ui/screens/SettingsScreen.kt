package com.zoomi.charades.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.zoomi.charades.BuildConfig
import com.zoomi.charades.data.AppTheme
import com.zoomi.charades.data.TiltSensitivity
import com.zoomi.charades.ui.components.ModalCloseButton
import com.zoomi.charades.ui.components.ModalScaffold
import com.zoomi.charades.ui.components.responsiveModalHeight
import com.zoomi.charades.ui.components.NeutralButton
import com.zoomi.charades.ui.components.OptionPill
import com.zoomi.charades.ui.components.hapticClick
import com.zoomi.charades.ui.theme.LocalExtendedColors
import com.zoomi.charades.ui.theme.hardShadow
import com.zoomi.charades.ui.theme.previewAccent
import com.zoomi.charades.ui.theme.primaryButtonColors
import com.zoomi.charades.ui.theme.primaryButtonGradientBackground
import com.zoomi.charades.ui.theme.themedGlow
import com.zoomi.charades.ui.theme.previewBackground
import com.zoomi.charades.ui.viewmodel.SettingsViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val ModalSurface: Color @Composable get() = LocalExtendedColors.current.modalSurface
private val BorderSlate800: Color @Composable get() = LocalExtendedColors.current.modalBorder
private val AmberBadgeBg: Color @Composable get() = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
private val Amber400: Color @Composable get() = LocalExtendedColors.current.accentBright
private val Amber500: Color @Composable get() = MaterialTheme.colorScheme.primary
private val Slate700: Color @Composable get() = MaterialTheme.colorScheme.outline
private val TextSlate100: Color @Composable get() = LocalExtendedColors.current.textStrong
private val TextSlate400: Color @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant
private val WhiteBorder10: Color @Composable get() = LocalExtendedColors.current.dividerFaint

private val ROUND_DURATION_OPTIONS = listOf(30, 60, 90, 120)
private const val PRIVACY_POLICY_URL = "https://loh-git.github.io/zoomi-privacy-policy/"
private const val FEEDBACK_EMAIL = "zoomistudios@outlook.com"

@Composable
fun SettingsScreen(viewModel: SettingsViewModel, onClose: () -> Unit) {
    val settings by viewModel.settings.collectAsState()
    val context = LocalContext.current
    var feedbackOpen by remember { mutableStateOf(false) }
    val extended = LocalExtendedColors.current

    val settingsModalShape = RoundedCornerShape(24.dp)
    ModalScaffold(onDismissRequest = onClose) {
        Column(
            modifier = Modifier
                .widthIn(max = 560.dp)
                .fillMaxWidth()
                .height(responsiveModalHeight(760.dp))
                .padding(horizontal = 16.dp)
                .hardShadow(LocalExtendedColors.current, settingsModalShape, large = true)
                .themedGlow(LocalExtendedColors.current, settingsModalShape, color = LocalExtendedColors.current.cardGlowColor, elevation = LocalExtendedColors.current.cardGlowElevation)
                .background(ModalSurface, settingsModalShape)
                .border(LocalExtendedColors.current.modalBorderWidth, BorderSlate800, settingsModalShape)
                .verticalScroll(rememberScrollState())
                .padding(extended.settingsContentPadding),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(AmberBadgeBg, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(imageVector = Icons.Filled.Settings, contentDescription = null, tint = Amber400, modifier = Modifier.size(20.dp))
                    }
                    Text(
                        text = "Game Settings",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = TextSlate100,
                    )
                }
                ModalCloseButton(onClick = onClose)
            }

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 24.dp)) {
                Icon(imageVector = Icons.Filled.Palette, contentDescription = null, tint = TextSlate400, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                SectionLabel("Visual Theme (${AppTheme.entries.size} Available)")
            }
            ThemeDropdownSelector(
                selected = settings.theme,
                onSelect = viewModel::setTheme,
                modifier = Modifier.padding(top = 8.dp),
            )
//            ThemeDetailsCard(theme = settings.theme, modifier = Modifier.padding(top = 12.dp))

            SectionLabel("Default Round Duration", modifier = Modifier.padding(top = 24.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ROUND_DURATION_OPTIONS.forEach { seconds ->
                    OptionPill(
                        label = "${seconds}s",
                        selected = settings.defaultRoundDurationSeconds == seconds,
                        onClick = { viewModel.setRoundDuration(seconds) },
                        modifier = Modifier.weight(1f),
                        selectedGradient = extended.timerOptionGradient,
                    )
                }
            }

            SectionLabel("Gyro Tilt Sensitivity", modifier = Modifier.padding(top = 20.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                TiltSensitivity.entries.forEach { sensitivity ->
                    OptionPill(
                        label = sensitivity.name,
                        selected = settings.tiltSensitivity == sensitivity,
                        onClick = { viewModel.setTiltSensitivity(sensitivity) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            ToggleRow(
                title = "Sound Effects",
                subtitle = "Audio chimes for timer and actions",
                checked = settings.soundEnabled,
                onCheckedChange = viewModel::setSoundEnabled,
                modifier = Modifier.padding(top = 20.dp),
            )
            ToggleRow(
                title = "Haptic Feedback",
                subtitle = "Enable/disable haptics",
                checked = settings.hapticsEnabled,
                onCheckedChange = viewModel::setHapticsEnabled,
            )
            ToggleRow(
                title = "Fullscreen Mode",
                subtitle = "Hide status bar and system buttons",
                checked = settings.fullscreenModeEnabled,
                onCheckedChange = viewModel::setFullscreenModeEnabled,
            )
            ToggleRow(
                title = "Touch Control Fallback",
                subtitle = "Display on-screen buttons",
                checked = settings.touchFallbackEnabled,
                onCheckedChange = viewModel::setTouchFallbackEnabled,
            )
            ToggleRow(
                title = "Invert Tilt Direction",
                subtitle = "Default: DOWN = Pass, UP = Correct",
                checked = settings.invertTilt,
                onCheckedChange = viewModel::setInvertTilt,
            )

            HorizontalDivider(modifier = Modifier.padding(top = 8.dp, bottom = 8.dp), color = WhiteBorder10)
            SettingsLinkRow(
                icon = Icons.Filled.Email,
                label = "Submit Feedback",
                onClick = { feedbackOpen = true },
                modifier = Modifier.padding(top = 8.dp),
            )
            SettingsLinkRow(
                icon = Icons.Filled.Lock,
                label = "Privacy Policy",
                onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_POLICY_URL))) },
                modifier = Modifier.padding(top = 8.dp),
            )
            Text(
                text = "App Version: ${BuildConfig.VERSION_NAME}",
                fontSize = 14.sp, // doubled from the original half-size (7.sp) per a follow-up request
                color = TextSlate100.copy(alpha = 0.5f), // same white, more faded
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            )
        }
    }

    if (feedbackOpen) {
        SubmitFeedbackDialog(onClose = { feedbackOpen = false })
    }
}

@Composable
private fun SettingsLinkRow(icon: ImageVector, label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, WhiteBorder10, RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = hapticClick(onClick),
            )
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = Amber400, modifier = Modifier.size(14.dp))
            Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextSlate100)
        }
    }
}

@Composable
private fun SubmitFeedbackDialog(onClose: () -> Unit) {
    val context = LocalContext.current
    var message by remember { mutableStateOf("") }

    val feedbackModalShape = RoundedCornerShape(24.dp)
    ModalScaffold(onDismissRequest = onClose) {
        Column(
            modifier = Modifier
                .widthIn(max = 480.dp)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .hardShadow(LocalExtendedColors.current, feedbackModalShape, large = true)
                .themedGlow(LocalExtendedColors.current, feedbackModalShape, color = LocalExtendedColors.current.cardGlowColor, elevation = LocalExtendedColors.current.cardGlowElevation)
                .background(ModalSurface, feedbackModalShape)
                .border(LocalExtendedColors.current.modalBorderWidth, BorderSlate800, feedbackModalShape)
                .padding(24.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = "Submit Feedback", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextSlate100)
                ModalCloseButton(onClick = onClose)
            }
            Text(
                text = "Tell us what's working, what isn't, or what you'd like to see. This opens your email app to send " +
                    "the message — your email address will be visible to us as the sender, and we'll attach your device " +
                    "model and the submission time for our records. We don't collect anything beyond that.",
                fontSize = 12.sp,
                color = TextSlate400,
                modifier = Modifier.padding(top = 8.dp),
            )
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                placeholder = { Text("What's on your mind?", color = TextSlate400, fontSize = 14.sp) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = LocalExtendedColors.current.inputSurface,
                    unfocusedContainerColor = LocalExtendedColors.current.inputSurface,
                    focusedBorderColor = Amber500,
                    unfocusedBorderColor = Slate700,
                    cursorColor = Amber500,
                    focusedTextColor = TextSlate100,
                    unfocusedTextColor = TextSlate100,
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(140.dp).padding(top = 16.dp),
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                NeutralButton(
                    text = "Cancel",
                    onClick = onClose,
                    modifier = Modifier.weight(1f).height(48.dp),
                )
                val sendExtended = LocalExtendedColors.current
                val sendShape = RoundedCornerShape(12.dp)
                val sendEnabled = message.isNotBlank()
                Button(
                    onClick = hapticClick {
                        if (sendFeedbackEmail(context, message)) onClose()
                    },
                    enabled = sendEnabled,
                    modifier = Modifier.weight(1f).height(48.dp)
                        .hardShadow(sendExtended, sendShape, large = true)
                        .themedGlow(sendExtended, sendShape, color = sendExtended.primaryButtonGlowColor, elevation = sendExtended.primaryButtonGlowElevation)
                        .primaryButtonGradientBackground(sendExtended, sendShape)
                        .then(if (sendExtended.primaryButtonGradient != null && !sendEnabled) Modifier.alpha(0.4f) else Modifier),
                    shape = sendShape,
                    colors = primaryButtonColors(sendExtended),
                    border = if (sendExtended.primaryButtonBorderWidth > 0.dp) BorderStroke(sendExtended.primaryButtonBorderWidth, sendExtended.primaryButtonBorderColor) else null,
                ) {
                    Text("Send", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// Manufacturer/model/OS version identify the hardware, not the person — no name, account, or
// device identifier (IMEI, Android ID, ad ID) goes into the prefix this app builds. Sent via
// ACTION_SENDTO so the user's own email app composes and sends it — this app never touches an
// SMTP credential, but that also means the recipient will see the user's email address as the
// sender, since that's attached by their email app, not by us; the UI copy discloses this.
// Returns false (and leaves the draft in place) if no app on the device can handle it.
private fun sendFeedbackEmail(context: Context, message: String): Boolean {
    val timestamp = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now())
    val body = "Device: ${Build.MANUFACTURER} ${Build.MODEL}, Android ${Build.VERSION.RELEASE}\n" +
        "Submitted: $timestamp\n\n$message"
    val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:")).apply {
        putExtra(Intent.EXTRA_EMAIL, arrayOf(FEEDBACK_EMAIL))
        putExtra(Intent.EXTRA_SUBJECT, "Ultimate Charades Feedback")
        putExtra(Intent.EXTRA_TEXT, body)
    }
    if (intent.resolveActivity(context.packageManager) == null) {
        Toast.makeText(context, "No email app found to send feedback.", Toast.LENGTH_LONG).show()
        return false
    }
    context.startActivity(intent)
    return true
}

@Composable
private fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        color = TextSlate400,
        modifier = modifier,
    )
}

@Composable
private fun ThemeDropdownSelector(selected: AppTheme, onSelect: (AppTheme) -> Unit, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    var triggerHeightPx by remember { mutableStateOf(0) }
    val shape = RoundedCornerShape(12.dp)
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { triggerHeightPx = it.size.height }
                .background(LocalExtendedColors.current.dropdownSurface, shape)
                .border(1.dp, Slate700, shape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = hapticClick { expanded = true },
                )
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = selected.label, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextSlate100)
            Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = null, tint = TextSlate100, modifier = Modifier.size(24.dp))
        }
        if (expanded) {
            Popup(
                alignment = Alignment.TopStart,
                offset = IntOffset(0, triggerHeightPx + with(LocalDensity.current) { 4.dp.roundToPx() }),
                onDismissRequest = { expanded = false },
                properties = PopupProperties(focusable = true),
            ) {
                Surface(
                    modifier = Modifier.width(maxWidth),
                    shape = shape,
                    color = LocalExtendedColors.current.dropdownSurface,
                    border = BorderStroke(1.dp, Slate700),
                    tonalElevation = 3.dp,
                    shadowElevation = 3.dp,
                ) {
                    Column {
                        AppTheme.entries.forEach { theme ->
                            Text(
                                text = theme.label,
                                fontSize = 14.sp,
                                color = TextSlate100,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(onClick = hapticClick {
                                        onSelect(theme)
                                        expanded = false
                                    })
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ThemeDetailsCard(theme: AppTheme, modifier: Modifier = Modifier) {
    val extended = LocalExtendedColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(extended.inputSurface, RoundedCornerShape(16.dp))
            .border(1.dp, extended.themePreviewBorderColor ?: Amber500.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(theme.previewBackground, RoundedCornerShape(12.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(theme.previewAccent, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(imageVector = Icons.Filled.Palette, contentDescription = null, tint = Color.White, modifier = Modifier.size(10.dp))
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(theme.previewBackground, CircleShape)
                        .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape),
                )
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(theme.previewAccent, CircleShape)
                        .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape),
                )
            }
        }
        Column {
            Text(text = theme.label, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = TextSlate100)
            Text(
                text = theme.description,
                fontSize = 12.sp,
                color = TextSlate400,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
    }
}

// Newer version of ThemeDetailsCard — a generic "this is about theming" palette icon (colored
// from the active theme's own switch-accent/page-background/page-border tokens) with just the
// theme name, instead of the per-theme preview dots + description look above. Kept here, not
// deleted, in case that look comes back. To restore it: uncomment this and delete the active
// ThemeDetailsCard above (its previewBackground/previewAccent dependency in ui/theme/Color.kt
// and this file's imports of them can stay either way — they're harmless if unused).
// @Composable
// private fun ThemeDetailsCard(theme: AppTheme, modifier: Modifier = Modifier) {
//     val extended = LocalExtendedColors.current
//     Row(
//         modifier = modifier
//             .fillMaxWidth()
//             .background(extended.inputSurface, RoundedCornerShape(16.dp))
//             .border(1.dp, extended.themePreviewBorderColor ?: Amber500.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
//             .padding(16.dp),
//         horizontalArrangement = Arrangement.spacedBy(12.dp),
//         verticalAlignment = Alignment.CenterVertically,
//     ) {
//         Box(
//             modifier = Modifier
//                 .size(48.dp)
//                 .background(ModalSurface, RoundedCornerShape(12.dp))
//                 .border(1.dp, BorderSlate800, RoundedCornerShape(12.dp)),
//             contentAlignment = Alignment.Center,
//         ) {
//             Icon(imageVector = Icons.Filled.Palette, contentDescription = null, tint = Amber500, modifier = Modifier.size(22.dp))
//         }
//         Text(text = theme.label, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = TextSlate100)
//     }
// }

@Composable
private fun ToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        HorizontalDivider(color = WhiteBorder10)
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextSlate100)
                Text(text = subtitle, fontSize = 12.sp, color = TextSlate400)
            }
            ToggleSwitch(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}

@Composable
private fun ToggleSwitch(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    val thumbOffset by animateDpAsState(targetValue = if (checked) 24.dp else 4.dp, label = "toggleThumb")
    Box(
        modifier = Modifier
            .size(width = 48.dp, height = 28.dp)
            .background(if (checked) Amber500 else Slate700, RoundedCornerShape(50))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = hapticClick { onCheckedChange(!checked) },
            ),
    ) {
        Box(
            modifier = Modifier
                .offset(x = thumbOffset, y = 4.dp)
                .size(20.dp)
                .background(LocalExtendedColors.current.toggleThumb, CircleShape),
        )
    }
}
