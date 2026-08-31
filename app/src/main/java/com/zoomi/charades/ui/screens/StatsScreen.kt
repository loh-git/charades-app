package com.zoomi.charades.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zoomi.charades.data.GameStats
import com.zoomi.charades.ui.components.ImmersiveDialog
import com.zoomi.charades.ui.components.ModalCloseButton
import com.zoomi.charades.ui.components.ModalScaffold
import com.zoomi.charades.ui.components.responsiveModalHeight
import com.zoomi.charades.ui.components.NeutralButton
import com.zoomi.charades.ui.components.hapticClick
import com.zoomi.charades.ui.theme.LocalExtendedColors
import com.zoomi.charades.ui.viewmodel.StatsViewModel

private val CardSurface: Color @Composable get() = LocalExtendedColors.current.modalSurface
private val TileSurface: Color @Composable get() = LocalExtendedColors.current.let { it.cardBackgroundOverride ?: it.modalBorder.copy(alpha = 0.9f) }
private val MetricTileSurface: Color @Composable get() = LocalExtendedColors.current.let { it.cardBackgroundOverride ?: it.modalBorder.copy(alpha = 0.6f) }
private val BorderSlate800: Color @Composable get() = LocalExtendedColors.current.modalBorder
private val BorderSlate700: Color @Composable get() = MaterialTheme.colorScheme.outline
private val TextSlate100: Color @Composable get() = MaterialTheme.colorScheme.onSurface
private val TextSlate400: Color @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant
private val Amber400: Color @Composable get() = LocalExtendedColors.current.accentBright
private val Amber500: Color @Composable get() = MaterialTheme.colorScheme.primary
private val Rose400 = Color(0xFFFB7185)

// Matches GameScreen's round-feedback flash colors exactly, so these tiles read as the same
// signal as a correct/passed answer during a round.
private val RoundCorrectColor = Color(0xFF00D492)
private val RoundPassColor = Color(0xFFFF2056)

@Composable
fun StatsScreen(viewModel: StatsViewModel, onClose: () -> Unit) {
    val stats by viewModel.stats.collectAsState()
    var showResetConfirm by remember { mutableStateOf(false) }

    ModalScaffold(onDismissRequest = onClose) {
        Column(
            modifier = Modifier
                .widthIn(max = 560.dp)
                .fillMaxWidth()
                .height(responsiveModalHeight(590.dp))
                .padding(horizontal = 16.dp)
                .background(CardSurface, RoundedCornerShape(24.dp))
                .border(1.dp, BorderSlate800, RoundedCornerShape(24.dp))
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Amber500.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(imageVector = Icons.Filled.BarChart, contentDescription = null, tint = Amber400, modifier = Modifier.size(20.dp))
                    }
                    Text(
                        text = "Your Stats",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = TextSlate100,
                    )
                }
                ModalCloseButton(onClick = onClose)
            }

            BestScoreCard(stats, modifier = Modifier.padding(top = 24.dp))

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                MetricTile(
                    label = "ROUNDS PLAYED",
                    value = stats.totalRoundsPlayed.toString(),
                    valueColor = TextSlate100,
                    modifier = Modifier.weight(1f),
                )
                MetricTile(
                    label = "LONGEST STREAK",
                    value = stats.longestStreak.toString(),
                    valueColor = TextSlate100,
                    modifier = Modifier.weight(1f),
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                MetricTile(
                    label = "TOTAL CORRECT",
                    value = stats.totalCorrect.toString(),
                    valueColor = RoundCorrectColor,
                    modifier = Modifier.weight(1f),
                )
                MetricTile(
                    label = "TOTAL PASSED",
                    value = stats.totalPassed.toString(),
                    valueColor = Rose400,
                    modifier = Modifier.weight(1f),
                )
            }

            Box(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .fillMaxWidth()
                    .background(LocalExtendedColors.current.dividerFaint)
                    .height(1.dp),
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.clickable(onClick = hapticClick { showResetConfirm = true }),
                ) {
                    Icon(imageVector = Icons.Filled.Delete, contentDescription = null, tint = Rose400, modifier = Modifier.size(14.dp))
                    Text(
                        text = "Reset Stats",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Rose400,
                    )
                }
                NeutralButton(text = "Close", onClick = onClose)
            }
        }
    }

    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = { ImmersiveDialog(); Text("Reset Stats?") },
            text = { Text("This permanently clears your rounds played, best score, streak history and total correct/passed.") },
            confirmButton = {
                TextButton(onClick = hapticClick {
                    viewModel.onResetStats()
                    showResetConfirm = false
                }) {
                    Text("Reset", color = Rose400)
                }
            },
            dismissButton = {
                TextButton(onClick = hapticClick { showResetConfirm = false }) {
                    Text("Cancel", color = LocalExtendedColors.current.neutralButtonText)
                }
            },
        )
    }
}

@Composable
private fun BestScoreCard(stats: GameStats, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(TileSurface, RoundedCornerShape(16.dp))
            .border(1.dp, BorderSlate700, RoundedCornerShape(16.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "BEST SCORE",
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.5.sp,
            color = TextSlate400,
        )
        Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.padding(top = 4.dp)) {
            Text(
                text = stats.bestScore.toString(),
                fontSize = 60.sp,
                fontWeight = FontWeight.Black,
                color = Amber400,
            )
            Text(
                text = " PTS",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Amber400,
                modifier = Modifier.padding(bottom = 8.dp),
            )
        }
        Text(
            text = if (stats.totalRoundsPlayed == 0) "No games played yet" else "Deck: ${stats.bestScoreDeckTitle}",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextSlate400,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun MetricTile(label: String, value: String, valueColor: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .height(100.dp)
            .background(MetricTileSurface, RoundedCornerShape(16.dp))
            .border(1.dp, BorderSlate700, RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = value,
            fontSize = 30.sp,
            fontWeight = FontWeight.Black,
            color = valueColor,
        )
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = TextSlate400,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}
