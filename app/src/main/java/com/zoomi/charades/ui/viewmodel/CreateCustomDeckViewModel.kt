package com.zoomi.charades.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.zoomi.charades.data.Category
import com.zoomi.charades.data.CustomCategoryRepository
import com.zoomi.charades.data.Deck
import com.zoomi.charades.data.DeckRepository
import java.util.UUID
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val MIN_WORDS = 1

class CreateCustomDeckViewModel(
    private val deckRepository: DeckRepository,
    private val customCategoryRepository: CustomCategoryRepository,
) : ViewModel() {

    var title by mutableStateOf("")
        private set
    var category by mutableStateOf(Category.MOVIES)
        private set
    // Non-null when the user picked (or just created) a custom category name instead of a
    // built-in one — takes precedence over `category` at save time.
    var selectedCustomCategory by mutableStateOf<String?>(null)
        private set
    var description by mutableStateOf("")
        private set
    var howToPlay by mutableStateOf("")
        private set
    var wordInput by mutableStateOf("")
        private set
    var words by mutableStateOf<List<String>>(emptyList())
        private set

    val customCategoryNames: StateFlow<List<String>> = customCategoryRepository.customCategoryNames
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val isValid: Boolean
        get() = title.isNotBlank() && words.size >= MIN_WORDS

    fun onTitleChange(value: String) {
        title = value
    }

    fun onCategoryChange(value: Category) {
        category = value
        selectedCustomCategory = null
    }

    fun onCustomCategorySelect(name: String) {
        selectedCustomCategory = name
    }

    fun addCustomCategory(name: String) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        selectedCustomCategory = trimmed
        viewModelScope.launch { customCategoryRepository.addCategory(trimmed) }
    }

    fun onDescriptionChange(value: String) {
        description = value
    }

    fun onHowToPlayChange(value: String) {
        howToPlay = value
    }

    fun onWordInputChange(value: String) {
        wordInput = value
    }

    fun addWord() {
        words = mergeWords(words, wordInput)
        wordInput = ""
    }

    fun removeWord(index: Int) {
        words = words.toMutableList().apply { removeAt(index) }
    }

    fun save(onSaved: () -> Unit) {
        // Any text still sitting in the word input (not yet explicitly "Added") should still
        // count toward the deck when saving, so the user isn't punished for hitting Save
        // immediately after typing the last word instead of tapping Add first.
        val finalWords = mergeWords(words, wordInput)
        if (title.isBlank() || finalWords.size < MIN_WORDS) return
        words = finalWords
        wordInput = ""
        val customCategory = selectedCustomCategory
        viewModelScope.launch {
            deckRepository.addCustomDeck(
                Deck(
                    id = UUID.randomUUID().toString(),
                    title = title.trim(),
                    // A custom-category deck still needs a `category` enum value to satisfy the
                    // required field (used by swatchColor/filtering elsewhere) — RANDOM_OBJECTS is
                    // an inert default that's never shown, since customCategoryName takes
                    // precedence wherever the category is displayed.
                    category = if (customCategory != null) Category.RANDOM_OBJECTS else category,
                    icon = if (customCategory != null) "" else category.icon,
                    shortDescription = description.trim().ifBlank { "A custom deck." },
                    howToPlay = howToPlay.trim().ifBlank { "Act it out without speaking or making sounds!" },
                    words = finalWords,
                    isCustom = true,
                    customCategoryName = customCategory,
                ),
            )
            resetFields()
            onSaved()
        }
    }

    // The dialog that owns this ViewModel is just a boolean flag toggling visibility, not a nav
    // destination — closing and reopening it does not recreate the ViewModel, so without this the
    // next "Create Custom Deck" open would still show the just-saved deck's title, words, etc.
    private fun resetFields() {
        title = ""
        category = Category.MOVIES
        selectedCustomCategory = null
        description = ""
        howToPlay = ""
        wordInput = ""
        words = emptyList()
    }

    private fun mergeWords(existing: List<String>, input: String): List<String> {
        val existingLower = existing.map { it.lowercase() }.toMutableSet()
        val newWords = input.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() && existingLower.add(it.lowercase()) }
        return existing + newWords
    }

    class Factory(
        private val deckRepository: DeckRepository,
        private val customCategoryRepository: CustomCategoryRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CreateCustomDeckViewModel(deckRepository, customCategoryRepository) as T
        }
    }
}
