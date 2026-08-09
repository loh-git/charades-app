package com.zoomi.charades.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private object FavoritesKeys {
    val FAVORITE_DECK_IDS = stringSetPreferencesKey("favorite_deck_ids")
}

class FavoritesRepository(private val dataStore: DataStore<Preferences>) {

    val favoriteDeckIds: Flow<Set<String>> = dataStore.data.map { prefs ->
        prefs[FavoritesKeys.FAVORITE_DECK_IDS] ?: emptySet()
    }

    suspend fun toggleFavorite(deckId: String) {
        dataStore.edit { prefs ->
            val current = prefs[FavoritesKeys.FAVORITE_DECK_IDS] ?: emptySet()
            prefs[FavoritesKeys.FAVORITE_DECK_IDS] = if (deckId in current) current - deckId else current + deckId
        }
    }
}
