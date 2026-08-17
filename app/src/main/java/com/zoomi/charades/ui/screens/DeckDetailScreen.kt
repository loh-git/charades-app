package com.zoomi.charades.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.zoomi.charades.data.Deck
import com.zoomi.charades.ui.components.NeutralButton
import com.zoomi.charades.ui.components.OptionPill
import com.zoomi.charades.ui.theme.iconVector

private val TIMER_OPTIONS = listOf(30, 60, 90)

@Composable
fun DeckDetailScreen(
    deck: Deck,
    defaultTimerSeconds: Int,
    onStart: (timerSeconds: Int, isPartyMode: Boolean) -> Unit,
    onBack: () -> Unit,
) {
    var selectedTimer by remember(deck.id) {
        mutableIntStateOf(TIMER_OPTIONS.find { it == defaultTimerSeconds } ?: 60)
    }
    var isPartyMode by remember(deck.id) { mutableStateOf(false) }

    Dialog(onDismissRequest = onBack, properties = DialogProperties(usePlatformDefaultWidth = false)) {
    Column(
        modifier = Modifier
            .widthIn(max = 480.dp)
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(Color(0xFF0F172A), RoundedCornerShape(24.dp))
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(24.dp))
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .padding(top = 12.dp)
                .size(80.dp)
                .background(Color(0xFF3E2F20), RoundedCornerShape(24.dp))
                .border(1.dp, Color(0xFF6C4818), RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center,
        ) {
            if (deck.isCustom) {
                Text(text = deck.icon, fontSize = 34.sp)
            } else {
                Icon(
                    imageVector = deck.category.iconVector,
                    contentDescription = deck.category.displayName,
                    tint = Color(0xFFFFB900),
                    modifier = Modifier.size(36.dp),
                )
            }
        }

        Box(
            modifier = Modifier
                .padding(top = 16.dp)
                .background(Color(0xFF3E2F20), RoundedCornerShape(50))
                .border(1.dp, Color(0xFF6C4818), RoundedCornerShape(50))
                .padding(horizontal = 14.dp, vertical = 6.dp),
        ) {
            Text(
                text = "${(deck.customCategoryName ?: deck.category.displayName).uppercase()} • ${deck.words.size} CARDS",
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFFFFB900),
                fontWeight = FontWeight.Bold,
            )
        }

        Text(
            text = deck.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 12.dp),
        )
        Text(
            text = deck.shortDescription,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp),
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                .padding(16.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = null,
                    tint = Color(0xFFFFB900),
                    modifier = Modifier.size(18.dp),
                )
                Text(
                    text = "  HOW TO PLAY THIS DECK:",
                    style = MaterialTheme.typography.labelLarge,
                    color = LocalContentColor.current,
                    fontWeight = FontWeight.Bold,
                )
            }
            Text(
                text = deck.howToPlay,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp),
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            TiltInstructionRow(
                icon = Icons.Filled.ArrowDownward,
                accentColor = Color(0xFFFF2056),
                pillBackground = Color(0xFF30243B),
                pillLabel = "DOWN",
                suffix = "to Pass",
            )
            TiltInstructionRow(
                icon = Icons.Filled.ArrowUpward,
                accentColor = Color(0xFF00D492),
                pillBackground = Color(0xFF16343F),
                pillLabel = "UP",
                suffix = "for Correct",
                modifier = Modifier.padding(top = 8.dp),
            )
        }

        Text(
            text = "ROUND DURATION",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = Color(0xFF94A3B8),
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            TIMER_OPTIONS.forEach { seconds ->
                OptionPill(
                    label = "${seconds}s",
                    selected = seconds == selectedTimer,
                    onClick = { selectedTimer = seconds },
                    modifier = Modifier.weight(1f),
                )
            }
        }

        Text(
            text = "MODE",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = Color(0xFF94A3B8),
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OptionPill(
                label = "Classic",
                selected = !isPartyMode,
                onClick = { isPartyMode = false },
                modifier = Modifier.weight(1f),
            )
            OptionPill(
                label = "Party",
                selected = isPartyMode,
                onClick = { isPartyMode = true },
                modifier = Modifier.weight(1f),
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 48.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            NeutralButton(
                text = "Back",
                onClick = onBack,
                modifier = Modifier.weight(1f).height(48.dp),
            )
            Button(
                onClick = { onStart(selectedTimer, isPartyMode) },
                modifier = Modifier.weight(2f).height(48.dp),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text("▶  Start Game")
            }
        }
    }
    }
}

@Composable
private fun TiltInstructionRow(
    icon: ImageVector,
    accentColor: Color,
    pillBackground: Color,
    pillLabel: String,
    suffix: String,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(16.dp))
        Text(text = " Tilt ", style = MaterialTheme.typography.bodyMedium)
        Box(
            modifier = Modifier
                .background(pillBackground, RoundedCornerShape(6.dp))
                .border(1.dp, accentColor, RoundedCornerShape(6.dp))
                .padding(horizontal = 8.dp, vertical = 2.dp),
        ) {
            Text(
                text = pillLabel,
                style = MaterialTheme.typography.labelSmall,
                color = accentColor,
                fontWeight = FontWeight.Bold,
            )
        }
        Text(text = " $suffix", style = MaterialTheme.typography.bodyMedium)
    }
}
