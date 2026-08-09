package com.zoomi.charades.data

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class CustomDeckRepositoryTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    // DataStore keeps this scope's coroutine alive for its own lifetime, independent of any
    // single test's runTest scope — reusing runTest's scope here would make it hang waiting
    // for a coroutine that never completes.
    private val dataStoreScope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob())

    private fun newRepository() = CustomDeckRepository(
        PreferenceDataStoreFactory.create(scope = dataStoreScope) { File(tempFolder.root, "test.preferences_pb") },
    )

    @Test
    fun `starts empty`() = runTest {
        val repository = newRepository()
        assertTrue(repository.decks.first().isEmpty())
    }

    @Test
    fun `addDeck persists and is readable`() = runTest {
        val repository = newRepository()
        val deck = Deck(
            id = "1",
            title = "My Deck",
            category = Category.MOVIES,
            icon = "🎬",
            shortDescription = "desc",
            howToPlay = "play",
            words = listOf("A", "B"),
            isCustom = true,
        )

        repository.addDeck(deck)

        assertEquals(listOf(deck), repository.decks.first())
    }

    @Test
    fun `deleteDeck removes only the matching deck`() = runTest {
        val repository = newRepository()
        val keep = Deck("keep", "Keep", Category.MUSIC, "🎵", "d", "h", listOf("X"), true)
        val remove = Deck("remove", "Remove", Category.MUSIC, "🎵", "d", "h", listOf("Y"), true)
        repository.addDeck(keep)
        repository.addDeck(remove)

        repository.deleteDeck("remove")

        assertEquals(listOf(keep), repository.decks.first())
    }
}
