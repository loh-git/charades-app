package com.zoomi.charades.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val CUSTOM_DECKS_KEY = stringPreferencesKey("custom_decks")

class CustomDeckRepository(private val dataStore: DataStore<Preferences>) {

    val decks: Flow<List<Deck>> = dataStore.data.map { prefs -> prefs.decodeDecks() }

    suspend fun addDeck(deck: Deck) {
        dataStore.edit { prefs ->
            prefs[CUSTOM_DECKS_KEY] = Json.encodeToString(prefs.decodeDecks() + deck)
        }
    }

    suspend fun deleteDeck(id: String) {
        dataStore.edit { prefs ->
            prefs[CUSTOM_DECKS_KEY] = Json.encodeToString(prefs.decodeDecks().filterNot { it.id == id })
        }
    }

    private fun Preferences.decodeDecks(): List<Deck> {
        val json = this[CUSTOM_DECKS_KEY] ?: return emptyList()
        return runCatching { Json.decodeFromString<List<Deck>>(json) }.getOrDefault(emptyList())
    }
}
