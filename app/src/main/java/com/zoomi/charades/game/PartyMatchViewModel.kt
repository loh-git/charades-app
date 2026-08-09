package com.zoomi.charades.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class TeamScore(val name: String, val total: Int = 0)

data class PartyMatchUiState(
    val teams: List<TeamScore> = emptyList(),
    val currentTeamIndex: Int = 0,
    val lastTurnTeamName: String? = null,
    val matchEnded: Boolean = false,
) {
    val upNextTeamName: String? get() = teams.getOrNull(currentTeamIndex)?.name
}

class PartyMatchViewModel(teamNames: List<String>) : ViewModel() {

    private val _uiState = MutableStateFlow(
        PartyMatchUiState(teams = teamNames.map { TeamScore(name = it) }),
    )
    val uiState: StateFlow<PartyMatchUiState> = _uiState.asStateFlow()

    fun recordTurn(score: Int) {
        val state = _uiState.value
        if (state.matchEnded || state.teams.isEmpty()) return

        val currentIndex = state.currentTeamIndex
        val current = state.teams[currentIndex]
        val updatedTeams = state.teams.toMutableList().apply {
            set(currentIndex, current.copy(total = current.total + score))
        }

        _uiState.value = state.copy(
            teams = updatedTeams,
            currentTeamIndex = (currentIndex + 1) % updatedTeams.size,
            lastTurnTeamName = current.name,
        )
    }

    fun endMatch() {
        _uiState.value = _uiState.value.copy(matchEnded = true)
    }

    class Factory(private val teamNames: List<String>) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PartyMatchViewModel(teamNames) as T
        }
    }
}
