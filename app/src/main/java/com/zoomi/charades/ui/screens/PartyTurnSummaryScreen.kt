package com.zoomi.charades.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zoomi.charades.game.PartyMatchUiState
import com.zoomi.charades.game.WordResult
import com.zoomi.charades.ui.components.hapticClick
import com.zoomi.charades.ui.theme.LocalExtendedColors
import com.zoomi.charades.ui.theme.hardShadow
import com.zoomi.charades.ui.theme.primaryButtonColors
import com.zoomi.charades.ui.theme.primaryButtonGradientBackground
import com.zoomi.charades.ui.theme.themedGlow

@Composable
fun PartyTurnSummaryScreen(
    score: Int,
    results: List<WordResult>,
    partyState: PartyMatchUiState,
    onNextTurn: () -> Unit,
    onEndMatch: () -> Unit,
) {
    val correctCount = results.count { it.correct }
    val passedCount = results.size - correctCount

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text(
            text = "${partyState.lastTurnTeamName ?: "Your team"} scored $score!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "$correctCount correct · $passedCount passed",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp),
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 20.dp, bottom = 8.dp),
        ) {
            Icon(imageVector = Icons.Filled.EmojiEvents, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Scoreboard",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        }
        LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth()) {
            items(partyState.teams) { team ->
                val isUpNext = team.name == partyState.upNextTeamName
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .background(
                            if (isUpNext) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.background,
                            RoundedCornerShape(12.dp),
                        )
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isUpNext) {
                            Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = team.name,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (isUpNext) FontWeight.Bold else FontWeight.Normal,
                        )
                    }
                    Text(
                        text = "${team.total} pts",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }

        partyState.upNextTeamName?.let { nextTeam ->
            Text(
                text = "Next up: $nextTeam",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp),
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val extended = LocalExtendedColors.current
            val nextTurnShape = ButtonDefaults.shape
            Button(
                onClick = hapticClick(onNextTurn),
                modifier = Modifier.fillMaxWidth()
                    .hardShadow(extended, nextTurnShape, large = true)
                    .themedGlow(extended, nextTurnShape, color = extended.primaryButtonGlowColor, elevation = extended.primaryButtonGlowElevation)
                    .primaryButtonGradientBackground(extended, nextTurnShape),
                shape = nextTurnShape,
                colors = primaryButtonColors(extended),
                border = if (extended.primaryButtonBorderWidth > 0.dp) BorderStroke(extended.primaryButtonBorderWidth, extended.primaryButtonBorderColor) else null,
            ) {
                Text("Start ${partyState.upNextTeamName ?: "Next"}'s Turn")
                Spacer(modifier = Modifier.width(8.dp))
                Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
            }
            OutlinedButton(onClick = hapticClick(onEndMatch), modifier = Modifier.fillMaxWidth()) {
                Text("End Match")
            }
        }
    }
}
