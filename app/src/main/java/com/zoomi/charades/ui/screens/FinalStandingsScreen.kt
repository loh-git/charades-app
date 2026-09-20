package com.zoomi.charades.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zoomi.charades.game.PartyMatchUiState
import com.zoomi.charades.game.TeamScore
import com.zoomi.charades.ui.components.ScreenHeaderIcon
import com.zoomi.charades.ui.theme.LocalExtendedColors
import com.zoomi.charades.ui.theme.hardShadow
import com.zoomi.charades.ui.theme.primaryButtonColors
import com.zoomi.charades.ui.theme.primaryButtonGradientBackground
import com.zoomi.charades.ui.theme.themedGlow
import com.zoomi.charades.ui.components.hapticClick

@Composable
fun FinalStandingsScreen(partyState: PartyMatchUiState, onMainMenu: () -> Unit) {
    val ranked = partyState.teams.sortedByDescending { it.total }
    val topScore = ranked.firstOrNull()?.total
    val leaders = ranked.filter { it.total == topScore }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ScreenHeaderIcon(Icons.Filled.EmojiEvents, contentDescription = "Trophy", size = 56.dp, modifier = Modifier.padding(top = 16.dp))
        Text(
            text = "Final Standings",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 12.dp),
        )
        if (leaders.isNotEmpty()) {
            Text(
                text = when {
                    leaders.size == 1 -> "${leaders.first().name} wins with $topScore pts!"
                    else -> "It's a draw between ${joinTeamNames(leaders)} at $topScore pts!"
                },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(top = 8.dp),
            )
        }

        LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth().padding(top = 20.dp)) {
            itemsIndexed(ranked) { index, team ->
                // Competition ("1224") ranking: teams tied on score share a rank, and the next
                // distinct score skips ahead accordingly rather than just using list position.
                val rank = ranked.take(index).count { it.total > team.total } + 1
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                        .padding(16.dp),
                ) {
                    Text(
                        text = "#$rank  ${team.name}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "${team.total} pts",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }

        val extended = LocalExtendedColors.current
        val mainMenuShape = RoundedCornerShape(12.dp)
        Button(
            onClick = hapticClick(onMainMenu),
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                .hardShadow(extended, mainMenuShape, large = true)
                .themedGlow(extended, mainMenuShape, color = extended.primaryButtonGlowColor, elevation = extended.primaryButtonGlowElevation)
                .primaryButtonGradientBackground(extended, mainMenuShape),
            shape = mainMenuShape,
            colors = primaryButtonColors(extended),
            border = if (extended.primaryButtonBorderWidth > 0.dp) BorderStroke(extended.primaryButtonBorderWidth, extended.primaryButtonBorderColor) else null,
        ) {
            Icon(imageVector = Icons.Filled.Home, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Main Menu")
        }
    }
}

private fun joinTeamNames(teams: List<TeamScore>): String {
    val names = teams.map { it.name }
    return names.dropLast(1).joinToString(", ") + " & " + names.last()
}
