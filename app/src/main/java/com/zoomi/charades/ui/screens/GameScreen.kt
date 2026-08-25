package com.zoomi.charades.ui.screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.view.WindowManager
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zoomi.charades.ads.AdProvider
import com.zoomi.charades.data.AdsRepository
import com.zoomi.charades.data.Deck
import com.zoomi.charades.data.GameSettings
import com.zoomi.charades.data.SettingsRepository
import com.zoomi.charades.data.StatsRepository
import com.zoomi.charades.game.GameEvent
import com.zoomi.charades.game.GameFeedback
import com.zoomi.charades.game.GameViewModel
import com.zoomi.charades.game.PartyMatchViewModel
import com.zoomi.charades.game.RoundPhase
import com.zoomi.charades.game.TiltDetector
import com.zoomi.charades.game.hasAccelerometer
import com.zoomi.charades.ui.components.ModalScaffold
import com.zoomi.charades.ui.components.NeutralButton
import com.zoomi.charades.ui.components.hapticClick
import com.zoomi.charades.ui.theme.LocalExtendedColors
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// Safety-valve only, not an ad-SDK-tuned value: if showInterstitial()'s onDismissed callback is
// never invoked (a misbehaving vendor SDK), the exit action still fires after this long instead
// of stranding the player on the summary screen indefinitely.
private const val AD_DISMISS_TIMEOUT_MS = 8_000L

@Composable
fun GameScreen(
    deck: Deck,
    roundDurationSeconds: Int,
    settingsRepository: SettingsRepository,
    statsRepository: StatsRepository,
    adsRepository: AdsRepository,
    adProvider: AdProvider,
    onExit: () -> Unit,
    partyTeams: List<String>? = null,
) {
    val coroutineScope = rememberCoroutineScope()
    val viewModel: GameViewModel = viewModel(
        factory = GameViewModel.Factory(
            deck = deck,
            roundDurationSeconds = roundDurationSeconds,
            onRoundFinished = { deckTitle, score, results ->
                coroutineScope.launch { statsRepository.recordRound(deckTitle, score, results) }
            },
        ),
    )
    val partyMatchViewModel: PartyMatchViewModel? = if (partyTeams != null) {
        viewModel(factory = PartyMatchViewModel.Factory(partyTeams))
    } else {
        null
    }
    val state by viewModel.uiState.collectAsState()
    val settings by settingsRepository.settings.collectAsState(initial = GameSettings())
    val context = LocalContext.current
    val activity = context.findActivity()

    // Gate a round-exit action behind an optional interstitial, per ARCHITECTURE.md's round-exit
    // sequence diagram: record the round, check whether an ad is due, and only then perform the
    // action the player actually asked for. onDismissed is also guaranteed by a timeout fallback
    // so a stuck/misbehaving ad SDK can never soft-lock the player on the summary screen (this is
    // flagged as the highest-risk part of Task 5 in plan.md).
    fun leaveRound(onProceed: () -> Unit) {
        coroutineScope.launch {
            adsRepository.recordRoundCompleted()
            if (activity != null && adsRepository.shouldShowAd.first()) {
                val proceeded = AtomicBoolean(false)
                fun proceedOnce() {
                    if (proceeded.compareAndSet(false, true)) onProceed()
                }
                val timeoutJob = launch {
                    delay(AD_DISMISS_TIMEOUT_MS)
                    proceedOnce()
                }
                adProvider.showInterstitial(activity) {
                    timeoutJob.cancel()
                    coroutineScope.launch { adsRepository.recordAdShown() }
                    proceedOnce()
                }
            } else {
                onProceed()
            }
        }
    }

    if (partyMatchViewModel != null) {
        LaunchedEffect(state.phase) {
            if (state.phase == RoundPhase.FINISHED) {
                partyMatchViewModel.recordTurn(state.score)
            }
        }
    }

    val feedback = remember { GameFeedback(context) }
    feedback.soundEnabled = settings.soundEnabled
    feedback.hapticsEnabled = settings.hapticsEnabled
    DisposableEffect(Unit) {
        onDispose { feedback.release() }
    }

    LaunchedEffect(state.phase, state.countdownValue) {
        if (state.phase == RoundPhase.COUNTDOWN) {
            feedback.onCountdownTick()
        }
    }

    KeepScreenOn()
    LockOrientation(landscape = state.phase != RoundPhase.FINISHED)

    var flashColor by remember { mutableStateOf(Color.Transparent) }
    val animatedFlash by animateColorAsState(flashColor, label = "flash")
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            feedback.onEvent(event)
            flashColor = when (event) {
                GameEvent.CORRECT -> Color(0xFF00D492).copy(alpha = 0.35f)
                GameEvent.PASS -> Color(0xFFFF2056).copy(alpha = 0.35f)
                GameEvent.TIME_UP -> Color.Transparent
            }
            if (event != GameEvent.TIME_UP) {
                delay(350)
                flashColor = Color.Transparent
            }
        }
    }

    var tiltError by remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current
    if (state.phase == RoundPhase.PLAYING) {
        DisposableEffect(settings.tiltSensitivity, settings.invertTilt, lifecycleOwner) {
            val (downAction, upAction) = if (settings.invertTilt) {
                viewModel::onTiltCorrect to viewModel::onTiltPass
            } else {
                viewModel::onTiltPass to viewModel::onTiltCorrect
            }
            // Sensor setup is a nice-to-have on top of the always-available tap buttons, so any
            // unexpected failure here should fall back to tap controls rather than crash the round.
            val detector = try {
                TiltDetector(
                    context = context,
                    triggerAngleDegrees = settings.tiltSensitivity.triggerAngleDegrees,
                    onTiltDown = downAction,
                    onTiltUp = upAction,
                )
            } catch (e: Exception) {
                tiltError = true
                null
            }

            // A registered sensor listener keeps delivering events (and firing tilt actions +
            // sound) even while the app is backgrounded, unless explicitly unregistered — tie
            // registration to the Activity lifecycle rather than just composition, so switching
            // away from the app actually stops the round from reacting to tilts.
            val lifecycleObserver = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_RESUME -> try {
                        detector?.register()
                    } catch (e: Exception) {
                        tiltError = true
                    }
                    Lifecycle.Event.ON_PAUSE -> detector?.unregister()
                    else -> Unit
                }
            }
            lifecycleOwner.lifecycle.addObserver(lifecycleObserver)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
                detector?.unregister()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(animatedFlash),
    ) {
        when (state.phase) {
            RoundPhase.COUNTDOWN -> CountdownContent(
                value = state.countdownValue,
                invertTilt = settings.invertTilt,
                deckTitle = deck.title,
            )
            RoundPhase.PLAYING -> PlayingContent(
                word = state.currentWord,
                timeRemaining = state.timeRemaining,
                score = state.score,
                showTapFallback = settings.touchFallbackEnabled || !context.hasAccelerometer() || tiltError,
                onCorrect = viewModel::onTiltCorrect,
                onPass = viewModel::onTiltPass,
                onPause = viewModel::pause,
            )
            RoundPhase.FINISHED -> {
                if (partyMatchViewModel != null) {
                    val partyState by partyMatchViewModel.uiState.collectAsState()
                    if (partyState.matchEnded) {
                        FinalStandingsScreen(partyState = partyState, onMainMenu = onExit)
                    } else {
                        PartyTurnSummaryScreen(
                            score = state.score,
                            results = state.results,
                            partyState = partyState,
                            onNextTurn = { leaveRound(viewModel::startRound) },
                            onEndMatch = { leaveRound(partyMatchViewModel::endMatch) },
                        )
                    }
                } else {
                    RoundSummaryScreen(
                        deckTitle = deck.title,
                        timerSeconds = roundDurationSeconds,
                        score = state.score,
                        results = state.results,
                        onPlayAgain = { leaveRound(viewModel::startRound) },
                        onHome = { leaveRound(onExit) },
                    )
                }
            }
        }
    }

    if (state.isPaused) {
        PauseDialog(
            onResume = viewModel::resume,
            onExit = { leaveRound(onExit) },
        )
    }
}

@Composable
private fun PauseDialog(onResume: () -> Unit, onExit: () -> Unit) {
    ModalScaffold(onDismissRequest = onResume) {
        Column(
            modifier = Modifier
                .widthIn(max = 320.dp)
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .background(Color(0xFF0F172A), RoundedCornerShape(24.dp))
                .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(24.dp))
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "PAUSED",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = Color(0xFFF8FAFC),
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                NeutralButton(
                    text = "Exit",
                    onClick = onExit,
                    modifier = Modifier.weight(1f),
                )
                Button(
                    onClick = hapticClick(onResume),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B), contentColor = Color(0xFF020617)),
                    contentPadding = PaddingValues(vertical = 10.dp),
                ) {
                    Text("Resume", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}

@Composable
private fun CountdownContent(value: Int, invertTilt: Boolean, deckTitle: String) {
    val playfulAnimations = LocalExtendedColors.current.playfulAnimations
    val countdownScale = remember { Animatable(1f) }
    LaunchedEffect(value) {
        if (playfulAnimations) {
            countdownScale.snapTo(1.3f)
            countdownScale.animateTo(1f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        }
    }
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
                .padding(horizontal = 20.dp, vertical = 12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Smartphone,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "PLACE PHONE ON FOREHEAD!",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            Text(
                text = "Screen facing out towards teammates",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        Text(
            text = value.toString(),
            style = MaterialTheme.typography.displayLarge.copy(
                brush = LocalExtendedColors.current.accentGradient,
                fontSize = 96.sp,
            ),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.graphicsLayer {
                scaleX = countdownScale.value
                scaleY = countdownScale.value
            },
        )

        Row {
            Text(text = "Deck: ", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = deckTitle, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }

        val (downLabel, upLabel) = if (invertTilt) "CORRECT" to "PASS" else "PASS" to "CORRECT"
        val (downColor, upColor) = if (invertTilt) {
            Color(0xFF00D492) to Color(0xFFFF2056)
        } else {
            Color(0xFFFF2056) to Color(0xFF00D492)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "↓ TILT DOWN = $downLabel",
                color = downColor,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelLarge,
            )
            Text(text = "|", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                text = "↑ TILT UP = $upLabel",
                color = upColor,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

@Composable
private fun PlayingContent(
    word: String,
    timeRemaining: Int,
    score: Int,
    showTapFallback: Boolean,
    onCorrect: () -> Unit,
    onPass: () -> Unit,
    onPause: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Score: $score",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
            )
            Text(
                "⏱ ${timeRemaining}s",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
            )
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFF1E293B), RoundedCornerShape(12.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = hapticClick(onPause),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(imageVector = Icons.Filled.Pause, contentDescription = "Pause", tint = Color.White)
                }
            }
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = word,
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(24.dp),
            )
        }
        if (showTapFallback) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                TapFallbackButton(
                    label = "Pass",
                    icon = Icons.Filled.ArrowDownward,
                    accentColor = Color(0xFFFF2056),
                    backgroundColor = Color(0xFF30243B),
                    onClick = onPass,
                    modifier = Modifier.weight(1f),
                )
                TapFallbackButton(
                    label = "Correct",
                    icon = Icons.Filled.ArrowUpward,
                    accentColor = Color(0xFF00D492),
                    backgroundColor = Color(0xFF16343F),
                    onClick = onCorrect,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

// Matches DeckDetailScreen.kt's TiltInstructionRow "Down"/"Up" pill colouring, scaled up into a
// full tap-fallback button so the two controls read as the same visual language.
@Composable
private fun TapFallbackButton(
    label: String,
    icon: ImageVector,
    accentColor: Color,
    backgroundColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor, contentColor = accentColor),
        border = BorderStroke(1.dp, accentColor),
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(label, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun KeepScreenOn() {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val activity = context.findActivity()
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }
}

@Composable
private fun LockOrientation(landscape: Boolean) {
    val context = LocalContext.current
    DisposableEffect(landscape) {
        val activity = context.findActivity()
        activity.setOrientationSafely(
            if (landscape) ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE else ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,
        )
        onDispose {
            activity.setOrientationSafely(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        }
    }
}

// Android 8.0/8.1 (API 26/27) has a documented OS bug where setRequestedOrientation() throws
// "Only fullscreen activities can request orientation" for activities using edge-to-edge
// (transparent/translucent system bars) content, even though the activity is opaque and
// fullscreen. Swallowing it here is the standard workaround; worst case the orientation
// request is silently ignored on those OS versions instead of crashing the app.
private fun Activity?.setOrientationSafely(orientation: Int) {
    try {
        this?.requestedOrientation = orientation
    } catch (_: IllegalStateException) {
    }
}

private fun Context.findActivity(): Activity? {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}
