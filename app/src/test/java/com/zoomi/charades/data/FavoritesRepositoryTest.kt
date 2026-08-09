package com.zoomi.charades.data

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class FavoritesRepositoryTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private val dataStoreScope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob())

    private fun newRepository() = FavoritesRepository(
        PreferenceDataStoreFactory.create(scope = dataStoreScope) { File(tempFolder.root, "favorites.preferences_pb") },
    )

    @Test
    fun `starts empty`() = runTest {
        assertTrue(newRepository().favoriteDeckIds.first().isEmpty())
    }

    @Test
    fun `toggleFavorite adds a deck id not already favorited`() = runTest {
        val repository = newRepository()

        repository.toggleFavorite("movies")

        assertEquals(setOf("movies"), repository.favoriteDeckIds.first())
    }

    @Test
    fun `toggleFavorite removes a deck id already favorited`() = runTest {
        val repository = newRepository()
        repository.toggleFavorite("movies")

        repository.toggleFavorite("movies")

        assertTrue(repository.favoriteDeckIds.first().isEmpty())
    }

    @Test
    fun `toggling one deck id does not affect others`() = runTest {
        val repository = newRepository()
        repository.toggleFavorite("movies")
        repository.toggleFavorite("animals")

        repository.toggleFavorite("movies")

        assertEquals(setOf("animals"), repository.favoriteDeckIds.first())
    }
}
