package com.zoomi.charades.game

import com.zoomi.charades.data.Category
import com.zoomi.charades.data.Deck
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

private const val TEST_ROUND_DURATION = 60

private val TEST_DECK = Deck(
    id = "test_deck",
    title = "Test Deck",
    category = Category.ANIMALS,
    icon = "🐾",
    shortDescription = "A deck for tests.",
    howToPlay = "Act it out!",
    words = listOf("Cat", "Dog", "Fish"),
)

@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun newViewModel() = GameViewModel(TEST_DECK, TEST_ROUND_DURATION)

    @Test
    fun `starts counting down immediately with no current word`() {
        val viewModel = newViewModel()
        assertEquals(RoundPhase.COUNTDOWN, viewModel.uiState.value.phase)
        assertEquals("", viewModel.uiState.value.currentWord)
    }

    @Test
    fun `countdown transitions into playing with a word`() {
        val viewModel = newViewModel()

        dispatcher.scheduler.advanceTimeBy(3_100)
        dispatcher.scheduler.runCurrent()

        assertEquals(RoundPhase.PLAYING, viewModel.uiState.value.phase)
        assertTrue(viewModel.uiState.value.currentWord.isNotEmpty())
        assertEquals(TEST_ROUND_DURATION, viewModel.uiState.value.timeRemaining)
    }

    @Test
    fun `correct tilt increments score and advances word`() = runTest {
        val viewModel = newViewModel()
        dispatcher.scheduler.advanceTimeBy(3_100)
        dispatcher.scheduler.runCurrent()

        val firstWord = viewModel.uiState.value.currentWord
        viewModel.onTiltCorrect()

        assertEquals(1, viewModel.uiState.value.score)
        assertEquals(listOf(WordResult(firstWord, correct = true)), viewModel.uiState.value.results)
        assertTrue(viewModel.uiState.value.currentWord != firstWord)
    }

    @Test
    fun `pass tilt does not change score but records the word`() = runTest {
        val viewModel = newViewModel()
        dispatcher.scheduler.advanceTimeBy(3_100)
        dispatcher.scheduler.runCurrent()

        val firstWord = viewModel.uiState.value.currentWord
        viewModel.onTiltPass()

        assertEquals(0, viewModel.uiState.value.score)
        assertEquals(listOf(WordResult(firstWord, correct = false)), viewModel.uiState.value.results)
    }

    @Test
    fun `round finishes when timer reaches zero`() {
        val viewModel = newViewModel()
        dispatcher.scheduler.advanceTimeBy((3 + TEST_ROUND_DURATION + 1) * 1000L)
        dispatcher.scheduler.runCurrent()

        assertEquals(RoundPhase.FINISHED, viewModel.uiState.value.phase)
    }

    @Test
    fun `round finishes when the deck is exhausted`() {
        val viewModel = newViewModel()
        dispatcher.scheduler.advanceTimeBy(3_100)
        dispatcher.scheduler.runCurrent()

        repeat(TEST_DECK.words.size) {
            if (viewModel.uiState.value.phase == RoundPhase.PLAYING) {
                viewModel.onTiltCorrect()
            }
        }

        assertEquals(RoundPhase.FINISHED, viewModel.uiState.value.phase)
        assertEquals(TEST_DECK.words.size, viewModel.uiState.value.score)
    }

    @Test
    fun `onRoundFinished fires with the deck title, score, and results when the round ends`() {
        var reportedDeckTitle: String? = null
        var reportedScore: Int? = null
        var reportedResults: List<WordResult>? = null
        val viewModel = GameViewModel(TEST_DECK, TEST_ROUND_DURATION) { deckTitle, score, results ->
            reportedDeckTitle = deckTitle
            reportedScore = score
            reportedResults = results
        }
        dispatcher.scheduler.advanceTimeBy(3_100)
        dispatcher.scheduler.runCurrent()

        repeat(TEST_DECK.words.size) {
            if (viewModel.uiState.value.phase == RoundPhase.PLAYING) {
                viewModel.onTiltCorrect()
            }
        }

        assertEquals(TEST_DECK.title, reportedDeckTitle)
        assertEquals(TEST_DECK.words.size, reportedScore)
        assertEquals(TEST_DECK.words.size, reportedResults?.size)
    }

    @Test
    fun `startRound resets score and results for a new attempt`() {
        val viewModel = newViewModel()
        dispatcher.scheduler.advanceTimeBy(3_100)
        dispatcher.scheduler.runCurrent()
        viewModel.onTiltCorrect()

        viewModel.startRound()

        assertEquals(RoundPhase.COUNTDOWN, viewModel.uiState.value.phase)
        assertEquals(0, viewModel.uiState.value.score)
        assertTrue(viewModel.uiState.value.results.isEmpty())
    }
}
