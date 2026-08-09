package com.zoomi.charades.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.zoomi.charades.data.GameStats
import com.zoomi.charades.data.StatsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class StatsViewModel(statsRepository: StatsRepository) : ViewModel() {

    val stats: StateFlow<GameStats> = statsRepository.stats
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), GameStats())

    class Factory(private val statsRepository: StatsRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return StatsViewModel(statsRepository) as T
        }
    }
}
