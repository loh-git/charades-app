package com.zoomi.charades.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zoomi.charades.game.PartyMatchUiState
import com.zoomi.charades.game.WordResult

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

        Text(
            text = "🏆 Scoreboard",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 20.dp, bottom = 8.dp),
        )
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
                ) {
                    Text(
                        text = if (isUpNext) "▶ ${team.name}" else team.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (isUpNext) FontWeight.Bold else FontWeight.Normal,
                    )
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
            Button(onClick = onNextTurn, modifier = Modifier.fillMaxWidth()) {
                Text("▶  Start ${partyState.upNextTeamName ?: "Next"}'s Turn")
            }
            OutlinedButton(onClick = onEndMatch, modifier = Modifier.fillMaxWidth()) {
                Text("End Match")
            }
        }
    }
}
