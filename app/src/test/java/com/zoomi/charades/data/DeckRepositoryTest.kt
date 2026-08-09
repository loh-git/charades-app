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

class DeckRepositoryTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private val dataStoreScope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob())

    private fun newRepository(): DeckRepository {
        val customRepository = CustomDeckRepository(
            PreferenceDataStoreFactory.create(scope = dataStoreScope) { File(tempFolder.root, "decks.preferences_pb") },
        )
        return DeckRepository(customRepository)
    }

    @Test
    fun `allDecks includes built-ins plus custom`() = runTest {
        val repository = newRepository()
        assertEquals(BuiltInDecks.all.size, repository.allDecks.first().size)

        val custom = Deck("c1", "Custom", Category.MUSIC, "🎵", "d", "h", listOf("X"), true)
        repository.addCustomDeck(custom)

        val after = repository.allDecks.first()
        assertEquals(BuiltInDecks.all.size + 1, after.size)
        assertTrue(after.contains(custom))
    }

    @Test
    fun `deckById finds a built-in deck by id`() = runTest {
        val repository = newRepository()
        val expected = BuiltInDecks.all.first()

        assertEquals(expected, repository.deckById(expected.id).first())
    }

    @Test
    fun `deckById returns null for an unknown id`() = runTest {
        val repository = newRepository()

        assertEquals(null, repository.deckById("does-not-exist").first())
    }
}
