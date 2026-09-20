package com.zoomi.charades.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zoomi.charades.ui.components.NeutralButton
import com.zoomi.charades.ui.components.hapticClick
import com.zoomi.charades.ui.theme.LocalExtendedColors
import com.zoomi.charades.ui.theme.hardShadow
import com.zoomi.charades.ui.theme.primaryButtonColors
import com.zoomi.charades.ui.theme.primaryButtonGradientBackground
import com.zoomi.charades.ui.theme.themedGlow

private const val MAX_TEAM_NAME_LENGTH = 25

@Composable
fun TeamSetupScreen(onStartMatch: (List<String>) -> Unit, onBack: () -> Unit) {
    var teamNames by remember { mutableStateOf(listOf("Team 1", "Team 2")) }
    val validNames = teamNames.map { it.trim() }.filter { it.isNotEmpty() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Filled.Celebration, contentDescription = null, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Party Mode",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
        }
        Text(
            text = "Who's playing? Teams take turns passing the phone.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp),
        )

        teamNames.forEachIndexed { index, name ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { updated ->
                        teamNames = teamNames.toMutableList().apply { set(index, updated.take(MAX_TEAM_NAME_LENGTH)) }
                    },
                    label = { Text("Team ${index + 1}") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                )
                if (teamNames.size > 2) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove team",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .clickable(onClick = hapticClick {
                                teamNames = teamNames.toMutableList().apply { removeAt(index) }
                            }),
                    )
                }
            }
        }

        val extended = LocalExtendedColors.current
        val addTeamShape = RoundedCornerShape(12.dp)
        val primaryButtonBorder = if (extended.primaryButtonBorderWidth > 0.dp) BorderStroke(extended.primaryButtonBorderWidth, extended.primaryButtonBorderColor) else null
        Button(
            onClick = hapticClick { teamNames = teamNames + "Team ${teamNames.size + 1}" },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                .hardShadow(extended, addTeamShape, large = true)
                .themedGlow(extended, addTeamShape, color = extended.primaryButtonGlowColor, elevation = extended.primaryButtonGlowElevation)
                .primaryButtonGradientBackground(extended, addTeamShape),
            shape = addTeamShape,
            colors = primaryButtonColors(extended),
            border = primaryButtonBorder,
        ) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add Team")
        }

        Spacer(modifier = Modifier.weight(1f))

        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val startMatchShape = RoundedCornerShape(12.dp)
            val startMatchEnabled = validNames.size >= 2
            Button(
                onClick = hapticClick { onStartMatch(validNames) },
                enabled = startMatchEnabled,
                modifier = Modifier.fillMaxWidth()
                    .hardShadow(extended, startMatchShape, large = true)
                    .themedGlow(extended, startMatchShape, color = extended.primaryButtonGlowColor, elevation = extended.primaryButtonGlowElevation)
                    .primaryButtonGradientBackground(extended, startMatchShape)
                    .then(if (extended.primaryButtonGradient != null && !startMatchEnabled) Modifier.alpha(0.4f) else Modifier),
                shape = startMatchShape,
                colors = primaryButtonColors(extended),
                border = primaryButtonBorder,
            ) {
                Text("Start Match")
                Spacer(modifier = Modifier.width(8.dp))
                Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
            }
            NeutralButton(text = "Back", onClick = onBack, modifier = Modifier.fillMaxWidth())
        }
    }
}
