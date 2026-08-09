package com.zoomi.charades.data

data class GameStats(
    val totalRoundsPlayed: Int = 0,
    val totalCorrect: Int = 0,
    val totalPassed: Int = 0,
    val bestScore: Int = 0,
    val bestScoreDeckTitle: String = "",
    val longestStreak: Int = 0,
)
