package com.zoomi.charades.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.zoomi.charades.data.Deck
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val COUNTDOWN_START = 3

enum class RoundPhase { COUNTDOWN, PLAYING, FINISHED }

enum class GameEvent { CORRECT, PASS, TIME_UP }

data class WordResult(val word: String, val correct: Boolean)

data class GameUiState(
    val phase: RoundPhase = RoundPhase.COUNTDOWN,
    val countdownValue: Int = COUNTDOWN_START,
    val currentWord: String = "",
    val timeRemaining: Int = 0,
    val score: Int = 0,
    val results: List<WordResult> = emptyList(),
    val isPaused: Boolean = false,
)

class GameViewModel(
    private val deck: Deck,
    private val roundDurationSeconds: Int,
    private val onRoundFinished: (deckTitle: String, score: Int, results: List<WordResult>) -> Unit = { _, _, _ -> },
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState(timeRemaining = roundDurationSeconds))
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<GameEvent>(extraBufferCapacity = 4)
    val events: SharedFlow<GameEvent> = _events

    private var remainingDeck: MutableList<String> = mutableListOf()
    private var roundJob: Job? = null

    init {
        startRound()
    }

    fun startRound() {
        roundJob?.cancel()
        remainingDeck = deck.words.shuffled().toMutableList()
        _uiState.value = GameUiState(
            phase = RoundPhase.COUNTDOWN,
            countdownValue = COUNTDOWN_START,
            timeRemaining = roundDurationSeconds,
        )
        roundJob = viewModelScope.launch {
            for (tick in COUNTDOWN_START downTo 1) {
                _uiState.value = _uiState.value.copy(countdownValue = tick)
                delay(1000)
            }
            beginPlaying()
        }
    }

    private suspend fun beginPlaying() {
        _uiState.value = _uiState.value.copy(
            phase = RoundPhase.PLAYING,
            currentWord = nextWordOrEmpty(),
            timeRemaining = roundDurationSeconds,
        )
        while (_uiState.value.timeRemaining > 0 && _uiState.value.phase == RoundPhase.PLAYING) {
            delay(1000)
            if (_uiState.value.phase != RoundPhase.PLAYING) return
            if (_uiState.value.isPaused) continue
            val next = _uiState.value.timeRemaining - 1
            _uiState.value = _uiState.value.copy(timeRemaining = next)
            if (next <= 0) {
                finishRound(timedOut = true)
            }
        }
    }

    fun pause() {
        if (_uiState.value.phase == RoundPhase.PLAYING) {
            _uiState.value = _uiState.value.copy(isPaused = true)
        }
    }

    fun resume() {
        _uiState.value = _uiState.value.copy(isPaused = false)
    }

    fun onTiltCorrect() = registerResult(correct = true)

    fun onTiltPass() = registerResult(correct = false)

    private fun registerResult(correct: Boolean) {
        val state = _uiState.value
        if (state.phase != RoundPhase.PLAYING || state.isPaused) return

        val word = state.currentWord
        val updated = state.copy(
            score = if (correct) state.score + 1 else state.score,
            results = state.results + WordResult(word, correct),
        )
        _events.tryEmit(if (correct) GameEvent.CORRECT else GameEvent.PASS)

        if (remainingDeck.isEmpty()) {
            _uiState.value = updated.copy(currentWord = "")
            finishRound(timedOut = false)
        } else {
            _uiState.value = updated.copy(currentWord = remainingDeck.removeAt(0))
        }
    }

    private fun finishRound(timedOut: Boolean) {
        roundJob?.cancel()
        if (timedOut) {
            _events.tryEmit(GameEvent.TIME_UP)
        }
        var finished = _uiState.value
        // The word on screen when time runs out was never explicitly marked correct or passed —
        // count it as incorrect rather than silently dropping it from the results/score.
        if (timedOut && finished.currentWord.isNotEmpty()) {
            finished = finished.copy(
                results = finished.results + WordResult(finished.currentWord, correct = false),
                currentWord = "",
            )
        }
        onRoundFinished(deck.title, finished.score, finished.results)
        _uiState.value = finished.copy(phase = RoundPhase.FINISHED, timeRemaining = 0)
    }

    private fun nextWordOrEmpty(): String =
        if (remainingDeck.isNotEmpty()) remainingDeck.removeAt(0) else ""

    override fun onCleared() {
        roundJob?.cancel()
    }

    class Factory(
        private val deck: Deck,
        private val roundDurationSeconds: Int,
        private val onRoundFinished: (deckTitle: String, score: Int, results: List<WordResult>) -> Unit = { _, _, _ -> },
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return GameViewModel(deck, roundDurationSeconds, onRoundFinished) as T
        }
    }
}
