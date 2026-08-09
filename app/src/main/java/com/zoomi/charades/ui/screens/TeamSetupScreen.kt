package com.zoomi.charades.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

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
        Text(
            text = "🎉 Party Mode",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
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
                        teamNames = teamNames.toMutableList().apply { set(index, updated) }
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
                            .clickable {
                                teamNames = teamNames.toMutableList().apply { removeAt(index) }
                            },
                    )
                }
            }
        }

        OutlinedButton(
            onClick = { teamNames = teamNames + "Team ${teamNames.size + 1}" },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        ) {
            Text("+  Add Team")
        }

        Spacer(modifier = Modifier.weight(1f))

        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Button(
                onClick = { onStartMatch(validNames) },
                enabled = validNames.size >= 2,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("▶  Start Match")
            }
            OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Text("Back")
            }
        }
    }
}
