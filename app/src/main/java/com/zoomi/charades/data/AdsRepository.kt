package com.zoomi.charades.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private object AdsKeys {
    val ROUNDS_SINCE_LAST_AD = intPreferencesKey("ads_rounds_since_last_ad")
}

class AdsRepository(
    private val dataStore: DataStore<Preferences>,
    private val roundsBetweenAds: Int,
) {

    val shouldShowAd: Flow<Boolean> = dataStore.data.map { prefs ->
        (prefs[AdsKeys.ROUNDS_SINCE_LAST_AD] ?: 0) >= roundsBetweenAds
    }

    suspend fun recordRoundCompleted() {
        dataStore.edit { prefs ->
            prefs[AdsKeys.ROUNDS_SINCE_LAST_AD] = (prefs[AdsKeys.ROUNDS_SINCE_LAST_AD] ?: 0) + 1
        }
    }

    suspend fun recordAdShown() {
        dataStore.edit { prefs -> prefs[AdsKeys.ROUNDS_SINCE_LAST_AD] = 0 }
    }
}
