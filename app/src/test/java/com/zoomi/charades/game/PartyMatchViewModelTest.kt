package com.zoomi.charades.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PartyMatchViewModelTest {

    private fun newViewModel(teamNames: List<String> = listOf("Team 1", "Team 2", "Team 3")) =
        PartyMatchViewModel(teamNames)

    @Test
    fun `starts with each team at zero and the first team up`() {
        val viewModel = newViewModel()

        val state = viewModel.uiState.value
        assertEquals(listOf("Team 1", "Team 2", "Team 3"), state.teams.map { it.name })
        assertTrue(state.teams.all { it.total == 0 })
        assertEquals(0, state.currentTeamIndex)
        assertFalse(state.matchEnded)
    }

    @Test
    fun `recordTurn adds to the current team and advances to the next`() {
        val viewModel = newViewModel()

        viewModel.recordTurn(5)

        val state = viewModel.uiState.value
        assertEquals(5, state.teams[0].total)
        assertEquals("Team 1", state.lastTurnTeamName)
        assertEquals(1, state.currentTeamIndex)
        assertEquals("Team 2", state.upNextTeamName)
    }

    @Test
    fun `turn rotation wraps around after the last team`() {
        val viewModel = newViewModel(listOf("A", "B"))

        viewModel.recordTurn(1)
        viewModel.recordTurn(2)
        viewModel.recordTurn(3)

        val state = viewModel.uiState.value
        assertEquals(4, state.teams[0].total)
        assertEquals(2, state.teams[1].total)
        assertEquals(1, state.currentTeamIndex)
    }

    @Test
    fun `endMatch marks the match ended and stops further scoring`() {
        val viewModel = newViewModel()
        viewModel.recordTurn(3)

        viewModel.endMatch()
        viewModel.recordTurn(10)

        val state = viewModel.uiState.value
        assertTrue(state.matchEnded)
        assertEquals(3, state.teams[0].total)
    }
}
