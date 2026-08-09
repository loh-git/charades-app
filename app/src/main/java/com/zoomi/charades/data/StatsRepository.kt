package com.zoomi.charades.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.zoomi.charades.game.WordResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private object StatsKeys {
    val TOTAL_ROUNDS = intPreferencesKey("stats_total_rounds")
    val TOTAL_CORRECT = intPreferencesKey("stats_total_correct")
    val TOTAL_PASSED = intPreferencesKey("stats_total_passed")
    val BEST_SCORE = intPreferencesKey("stats_best_score")
    val BEST_SCORE_DECK_TITLE = stringPreferencesKey("stats_best_score_deck_title")
    val LONGEST_STREAK = intPreferencesKey("stats_longest_streak")
}

class StatsRepository(private val dataStore: DataStore<Preferences>) {

    val stats: Flow<GameStats> = dataStore.data.map { prefs ->
        GameStats(
            totalRoundsPlayed = prefs[StatsKeys.TOTAL_ROUNDS] ?: 0,
            totalCorrect = prefs[StatsKeys.TOTAL_CORRECT] ?: 0,
            totalPassed = prefs[StatsKeys.TOTAL_PASSED] ?: 0,
            bestScore = prefs[StatsKeys.BEST_SCORE] ?: 0,
            bestScoreDeckTitle = prefs[StatsKeys.BEST_SCORE_DECK_TITLE] ?: "",
            longestStreak = prefs[StatsKeys.LONGEST_STREAK] ?: 0,
        )
    }

    suspend fun recordRound(deckTitle: String, score: Int, results: List<WordResult>) {
        val correctCount = results.count { it.correct }
        val passedCount = results.size - correctCount
        val streak = longestStreak(results)

        dataStore.edit { prefs ->
            prefs[StatsKeys.TOTAL_ROUNDS] = (prefs[StatsKeys.TOTAL_ROUNDS] ?: 0) + 1
            prefs[StatsKeys.TOTAL_CORRECT] = (prefs[StatsKeys.TOTAL_CORRECT] ?: 0) + correctCount
            prefs[StatsKeys.TOTAL_PASSED] = (prefs[StatsKeys.TOTAL_PASSED] ?: 0) + passedCount

            if (score > (prefs[StatsKeys.BEST_SCORE] ?: 0)) {
                prefs[StatsKeys.BEST_SCORE] = score
                prefs[StatsKeys.BEST_SCORE_DECK_TITLE] = deckTitle
            }
            if (streak > (prefs[StatsKeys.LONGEST_STREAK] ?: 0)) {
                prefs[StatsKeys.LONGEST_STREAK] = streak
            }
        }
    }

    private fun longestStreak(results: List<WordResult>): Int {
        var longest = 0
        var current = 0
        for (result in results) {
            current = if (result.correct) current + 1 else 0
            longest = maxOf(longest, current)
        }
        return longest
    }
}
