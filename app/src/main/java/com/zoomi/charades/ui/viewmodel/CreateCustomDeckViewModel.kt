package com.zoomi.charades.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.zoomi.charades.data.Category
import com.zoomi.charades.data.Deck
import com.zoomi.charades.data.DeckRepository
import java.util.UUID
import kotlinx.coroutines.launch

class CreateCustomDeckViewModel(private val deckRepository: DeckRepository) : ViewModel() {

    var title by mutableStateOf("")
        private set
    var category by mutableStateOf(Category.MOVIES)
        private set
    var description by mutableStateOf("")
        private set
    var howToPlay by mutableStateOf("")
        private set
    var wordsText by mutableStateOf("")
        private set

    val isValid: Boolean
        get() = title.isNotBlank() && parseWords(wordsText).isNotEmpty()

    fun onTitleChange(value: String) {
        title = value
    }

    fun onCategoryChange(value: Category) {
        category = value
    }

    fun onDescriptionChange(value: String) {
        description = value
    }

    fun onHowToPlayChange(value: String) {
        howToPlay = value
    }

    fun onWordsChange(value: String) {
        wordsText = value
    }

    fun save(onSaved: () -> Unit) {
        if (!isValid) return
        viewModelScope.launch {
            deckRepository.addCustomDeck(
                Deck(
                    id = UUID.randomUUID().toString(),
                    title = title.trim(),
                    category = category,
                    icon = category.icon,
                    shortDescription = description.trim().ifBlank { "A custom deck." },
                    howToPlay = howToPlay.trim().ifBlank { "Act it out without speaking or making sounds!" },
                    words = parseWords(wordsText),
                    isCustom = true,
                ),
            )
            onSaved()
        }
    }

    private fun parseWords(text: String): List<String> =
        text.split(",", "\n")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

    class Factory(private val deckRepository: DeckRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CreateCustomDeckViewModel(deckRepository) as T
        }
    }
}
