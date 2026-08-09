package com.zoomi.charades.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zoomi.charades.data.Deck
import com.zoomi.charades.ui.components.SelectablePill

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

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = deck.icon, fontSize = 56.sp, modifier = Modifier.padding(top = 12.dp))
        Text(
            text = deck.category.displayName.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 12.dp),
        )
        Text(
            text = deck.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp),
        )
        Text(
            text = deck.shortDescription,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 12.dp),
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
                .padding(16.dp),
        ) {
            Text(
                text = "❓ HOW TO PLAY THIS DECK:",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = deck.howToPlay,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp),
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = "⏱ Timer:", style = MaterialTheme.typography.bodyMedium)
            Row(
                modifier = Modifier.padding(start = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                TIMER_OPTIONS.forEach { seconds ->
                    SelectablePill(
                        label = "${seconds}s",
                        selected = seconds == selectedTimer,
                        onClick = { selectedTimer = seconds },
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = "🎮 Mode:", style = MaterialTheme.typography.bodyMedium)
            Row(
                modifier = Modifier.padding(start = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                SelectablePill(label = "Classic", selected = !isPartyMode, onClick = { isPartyMode = false })
                SelectablePill(label = "Party", selected = isPartyMode, onClick = { isPartyMode = true })
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
            Text("Back")
        }
        Button(
            onClick = { onStart(selectedTimer, isPartyMode) },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        ) {
            Text("▶  Start")
        }
    }
}
