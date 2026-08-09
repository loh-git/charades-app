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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zoomi.charades.game.WordResult
import com.zoomi.charades.ui.theme.CorrectGreen
import com.zoomi.charades.ui.theme.PassOrange

@Composable
fun RoundSummaryScreen(
    score: Int,
    results: List<WordResult>,
    onPlayAgain: () -> Unit,
    onHome: () -> Unit,
) {
    val correctCount = results.count { it.correct }
    val passedCount = results.size - correctCount
    val accuracy = if (results.isEmpty()) 0 else (correctCount * 100) / results.size

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.padding(top = 16.dp)) {
            Text(text = score.toString(), style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.Bold)
            Text(
                text = " PTS",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp),
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            StatColumn(label = "CORRECT", value = "+$correctCount", color = CorrectGreen)
            StatColumn(label = "PASSED", value = "$passedCount", color = PassOrange)
            StatColumn(label = "ACCURACY", value = "$accuracy%", color = MaterialTheme.colorScheme.secondary)
        }

        Text(
            text = "📊 Word Card Results (${results.size})",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp, bottom = 8.dp),
        )

        if (results.isEmpty()) {
            Text(
                text = "No cards played during round.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f).fillMaxWidth().padding(top = 8.dp),
            )
        } else {
            LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth()) {
                items(results) { result ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Text(
                            text = if (result.correct) "✓" else "✗",
                            color = if (result.correct) CorrectGreen else PassOrange,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(end = 8.dp),
                        )
                        Text(text = result.word, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedButton(onClick = onHome, modifier = Modifier.fillMaxWidth()) {
                Text("▦  Main Menu")
            }
            Button(onClick = onPlayAgain, modifier = Modifier.fillMaxWidth()) {
                Text("↺  PLAY AGAIN")
            }
        }
    }
}

@Composable
private fun StatColumn(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = color,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}
