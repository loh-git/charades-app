package com.zoomi.charades.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private object CustomCategoryKeys {
    val CUSTOM_CATEGORY_NAMES = stringSetPreferencesKey("custom_category_names")
}

class CustomCategoryRepository(private val dataStore: DataStore<Preferences>) {

    val customCategoryNames: Flow<List<String>> = dataStore.data.map { prefs ->
        (prefs[CustomCategoryKeys.CUSTOM_CATEGORY_NAMES] ?: emptySet()).sorted()
    }

    suspend fun addCategory(name: String) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        dataStore.edit { prefs ->
            val current = prefs[CustomCategoryKeys.CUSTOM_CATEGORY_NAMES] ?: emptySet()
            prefs[CustomCategoryKeys.CUSTOM_CATEGORY_NAMES] = current + trimmed
        }
    }
}
