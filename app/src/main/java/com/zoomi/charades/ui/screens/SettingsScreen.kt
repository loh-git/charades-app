package com.zoomi.charades.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zoomi.charades.data.AppTheme
import com.zoomi.charades.data.TiltSensitivity
import com.zoomi.charades.game.hasAccelerometer
import com.zoomi.charades.ui.components.SelectablePill
import com.zoomi.charades.ui.viewmodel.SettingsViewModel

private val ROUND_DURATION_OPTIONS = listOf(30, 60, 90)
private const val PRIVACY_POLICY_URL = "https://loh-git.github.io/zoomi-privacy-policy/"

@Composable
fun SettingsScreen(viewModel: SettingsViewModel, onClose: () -> Unit) {
    val settings by viewModel.settings.collectAsState()
    val context = LocalContext.current
    // val hasAccelerometer = remember { context.hasAccelerometer() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "🎛️ Game Settings",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.clickable(onClick = onClose).padding(4.dp),
            )
        }

        SectionLabel("Theme", topPadding = 24.dp)
        ThemeDropdown(
            selected = settings.theme,
            onSelect = viewModel::setTheme,
            modifier = Modifier.padding(top = 8.dp),
        )

        SectionLabel("Default Round Duration", topPadding = 24.dp)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
            ROUND_DURATION_OPTIONS.forEach { seconds ->
                SelectablePill(
                    label = "${seconds}s",
                    selected = settings.defaultRoundDurationSeconds == seconds,
                    onClick = { viewModel.setRoundDuration(seconds) },
                )
            }
        }

        SectionLabel("Gyro Tilt Sensitivity", topPadding = 24.dp)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
            TiltSensitivity.entries.forEach { sensitivity ->
                SelectablePill(
                    label = sensitivity.label.uppercase(),
                    selected = settings.tiltSensitivity == sensitivity,
                    onClick = { viewModel.setTiltSensitivity(sensitivity) },
                )
            }
        }

        SettingsToggleRow(
            title = "Sound Effects",
            description = "Audio chimes for timer and actions",
            checked = settings.soundEnabled,
            onCheckedChange = viewModel::setSoundEnabled,
        )
        SettingsToggleRow(
            title = "Touch Control Fallback",
            description = "Display manual Pass & Correct buttons",
            checked = settings.touchFallbackEnabled,
            onCheckedChange = viewModel::setTouchFallbackEnabled,
        )
        SettingsToggleRow(
            title = "Invert Tilt Direction",
            description = "Default: Tilt DOWN = Pass, Tilt UP = Correct",
            checked = settings.invertTilt,
            onCheckedChange = viewModel::setInvertTilt,
        )

        SectionLabel("About", topPadding = 24.dp)
        OutlinedButton(
            onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_POLICY_URL))) },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        ) {
            Text("🔒  Privacy Policy")
        }

        /*
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = "📱 Motion Sensor Status:", style = MaterialTheme.typography.bodyMedium)
            Text(
                text = if (hasAccelerometer) "Sensor Active" else "Fallback Mode",
                style = MaterialTheme.typography.bodyMedium,
                color = OrangeAccent,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp),
            )
        }

        var calibrationMessage by remember { mutableStateOf<String?>(null) }
        OutlinedButton(
            onClick = { calibrationMessage = "Calibration coming soon!" },
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
        ) {
            Text("↺  Calibrate Neutral Forehead Angle")
        }
        calibrationMessage?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
        */
    }
}

@Composable
private fun SectionLabel(text: String, topPadding: Dp = 0.dp) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = topPadding),
    )
}

@Composable
private fun ThemeDropdown(selected: AppTheme, onSelect: (AppTheme) -> Unit, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier.fillMaxWidth()) {
        OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(selected.label)
                Text("▾")
            }
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
private fun SettingsToggleRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            Text(text = description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
