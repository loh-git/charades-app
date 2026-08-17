package com.zoomi.charades.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.zoomi.charades.game.WordResult
import com.zoomi.charades.ui.components.NeutralButton
import com.zoomi.charades.ui.components.ScreenHeaderIcon
import com.zoomi.charades.ui.theme.CorrectGreen
import com.zoomi.charades.ui.theme.PassRose

@Composable
fun RoundSummaryScreen(
    deckTitle: String,
    timerSeconds: Int,
    score: Int,
    results: List<WordResult>,
    onPlayAgain: () -> Unit,
    onHome: () -> Unit,
) {
    val correctWords = results.filter { it.correct }
    val passedWords = results.filter { !it.correct }

    Dialog(onDismissRequest = onHome, properties = DialogProperties(usePlatformDefaultWidth = false)) {
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
        ScreenHeaderIcon(Icons.Filled.EmojiEvents, contentDescription = "Trophy", size = 56.dp, modifier = Modifier.padding(top = 8.dp))
        Text(
            text = "TIME'S UP!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 12.dp),
        )
        Text(
            text = "Deck: $deckTitle (${timerSeconds}s round)",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp),
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "FINAL SCORE",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold,
            )
            Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.padding(top = 4.dp)) {
                Text(text = score.toString(), style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.Bold)
                Text(
                    text = " PTS",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
            }
            Text(
                text = "Guessed ${correctWords.size} of ${results.size} words seen",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        ResultSection(
            title = "✓ CORRECT (${correctWords.size})",
            titleColor = CorrectGreen,
            words = correctWords,
            emptyMessage = "No correct guesses this round.",
            modifier = Modifier.padding(top = 20.dp),
        )
        ResultSection(
            title = "✗ PASSED (${passedWords.size})",
            titleColor = PassRose,
            words = passedWords,
            emptyMessage = "Zero passes — perfect streak!",
            modifier = Modifier.padding(top = 16.dp),
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            NeutralButton(
                text = "All Decks",
                onClick = onHome,
                modifier = Modifier.weight(1f).height(48.dp),
            )
            Button(
                onClick = onPlayAgain,
                modifier = Modifier.weight(1f).height(48.dp),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text("↺  Play Again")
            }
        }
    }
    }
}

@Composable
private fun ResultSection(
    title: String,
    titleColor: androidx.compose.ui.graphics.Color,
    words: List<WordResult>,
    emptyMessage: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = titleColor,
            fontWeight = FontWeight.Bold,
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                .padding(16.dp),
        ) {
            if (words.isEmpty()) {
                Text(
                    text = emptyMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontStyle = FontStyle.Italic,
                )
            } else {
                words.forEach { word ->
                    Text(
                        text = word.word,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 2.dp),
                    )
                }
            }
        }
    }
}
