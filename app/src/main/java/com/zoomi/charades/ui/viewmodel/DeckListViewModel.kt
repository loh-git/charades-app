package com.zoomi.charades.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.zoomi.charades.data.Category
import com.zoomi.charades.data.Deck
import com.zoomi.charades.data.DeckRepository
import com.zoomi.charades.data.FavoritesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface DeckFilter {
    data object All : DeckFilter
    data object Favourites : DeckFilter
    data object Custom : DeckFilter
    data class ByCategory(val category: Category) : DeckFilter
}

class DeckListViewModel(
    private val deckRepository: DeckRepository,
    private val favoritesRepository: FavoritesRepository,
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedFilter = MutableStateFlow<DeckFilter>(DeckFilter.All)
    val selectedFilter: StateFlow<DeckFilter> = _selectedFilter.asStateFlow()

    val allDecks: StateFlow<List<Deck>> = deckRepository.allDecks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val favoriteDeckIds: StateFlow<Set<String>> = favoritesRepository.favoriteDeckIds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    val filteredDecks: StateFlow<List<Deck>> = combine(
        deckRepository.allDecks,
        _searchQuery,
        _selectedFilter,
        favoritesRepository.favoriteDeckIds,
    ) { decks, query, filter, favoriteIds ->
        decks.filter { deck ->
            val matchesFilter = when (filter) {
                DeckFilter.All -> true
                DeckFilter.Favourites -> deck.id in favoriteIds
                DeckFilter.Custom -> deck.isCustom
                is DeckFilter.ByCategory -> deck.category == filter.category
            }
            matchesFilter && (query.isBlank() || deck.title.contains(query, ignoreCase = true))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onFilterSelected(filter: DeckFilter) {
        _selectedFilter.value = filter
    }

    fun onToggleFavorite(deckId: String) {
        viewModelScope.launch { favoritesRepository.toggleFavorite(deckId) }
    }

    class Factory(
        private val deckRepository: DeckRepository,
        private val favoritesRepository: FavoritesRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DeckListViewModel(deckRepository, favoritesRepository) as T
        }
    }
}
