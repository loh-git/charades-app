package com.zoomi.charades.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.zoomi.charades.data.AppTheme
import com.zoomi.charades.data.TiltSensitivity
import com.zoomi.charades.ui.components.ModalCloseButton
import com.zoomi.charades.ui.components.OptionPill
import com.zoomi.charades.ui.theme.previewAccent
import com.zoomi.charades.ui.theme.previewBackground
import com.zoomi.charades.ui.viewmodel.SettingsViewModel

private val ModalSurface = Color(0xFF0F172A) // slate-900, opaque
private val BorderSlate800 = Color(0xFF1E293B)
private val AmberBadgeBg = Color(0x33F59E0B) // amber-500/20
private val Amber400 = Color(0xFFFBBF24)
private val Amber500 = Color(0xFFF59E0B)
private val Slate700 = Color(0xFF334155)
private val TextSlate100 = Color(0xFFF8FAFC)
private val TextSlate400 = Color(0xFF94A3B8)
private val WhiteBorder10 = Color(0x1AFFFFFF) // white/10

private val ROUND_DURATION_OPTIONS = listOf(30, 60, 90)
private const val PRIVACY_POLICY_URL = "https://loh-git.github.io/zoomi-privacy-policy/"

@Composable
fun SettingsScreen(viewModel: SettingsViewModel, onClose: () -> Unit) {
    val settings by viewModel.settings.collectAsState()
    val context = LocalContext.current

    Dialog(onDismissRequest = onClose, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Column(
            modifier = Modifier
                .widthIn(max = 560.dp)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .background(ModalSurface, RoundedCornerShape(24.dp))
                .border(1.dp, BorderSlate800, RoundedCornerShape(24.dp))
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
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

            SectionLabel(
                "🎨 Visual Theme (${AppTheme.entries.size} Available)",
                modifier = Modifier.padding(top = 24.dp),
            )
            ThemeDropdownSelector(
                selected = settings.theme,
                onSelect = viewModel::setTheme,
                modifier = Modifier.padding(top = 8.dp),
            )
            ThemeDetailsCard(theme = settings.theme, modifier = Modifier.padding(top = 12.dp))

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
                title = "Touch Control Fallback",
                subtitle = "Display manual Pass & Correct buttons",
                checked = settings.touchFallbackEnabled,
                onCheckedChange = viewModel::setTouchFallbackEnabled,
            )
            ToggleRow(
                title = "Invert Tilt Direction",
                subtitle = "Default: Tilt DOWN = Pass, Tilt UP = Correct",
                checked = settings.invertTilt,
                onCheckedChange = viewModel::setInvertTilt,
            )

            HorizontalDivider(modifier = Modifier.padding(top = 8.dp, bottom = 8.dp), color = WhiteBorder10)
            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
                    .border(1.dp, WhiteBorder10, RoundedCornerShape(12.dp))
                    .clickable(
                        onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_POLICY_URL))) },
                    )
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(imageVector = Icons.Filled.Lock, contentDescription = null, tint = Amber400, modifier = Modifier.size(14.dp))
                    Text(text = "Privacy Policy & About", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSlate100)
                }
            }
        }
    }
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
    val shape = RoundedCornerShape(12.dp)
    Box(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BorderSlate800, shape)
                .border(1.dp, Slate700, shape)
                .clickable(onClick = { expanded = true })
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = selected.label, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextSlate100)
            Text(text = "▾", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextSlate100)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            AppTheme.entries.forEach { theme ->
                DropdownMenuItem(
                    text = { Text(theme.label) },
                    onClick = {
                        onSelect(theme)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun ThemeDetailsCard(theme: AppTheme, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xCC1E293B), RoundedCornerShape(16.dp)) // slate-800/80
            .border(1.dp, Amber500.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f, fill = false),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
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
            Column {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = theme.label, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextSlate100)
                    Box(
                        modifier = Modifier
                            .background(AmberBadgeBg, RoundedCornerShape(50))
                            .padding(horizontal = 8.dp),
                    ) {
                        Text(text = "Active", fontSize = 8.sp, lineHeight = 16.sp, color = Amber400)
                    }
                }
                Text(
                    text = theme.description,
                    fontSize = 12.sp,
                    color = TextSlate400,
                    modifier = Modifier.padding(top = 2.dp).width(185.dp),

                )
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
}

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
                onClick = { onCheckedChange(!checked) },
            ),
    ) {
        Box(
            modifier = Modifier
                .offset(x = thumbOffset, y = 4.dp)
                .size(20.dp)
                .background(Color.White, CircleShape),
        )
    }
}
