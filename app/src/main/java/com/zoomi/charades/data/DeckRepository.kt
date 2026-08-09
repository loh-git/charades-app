package com.zoomi.charades.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DeckRepository(private val customDeckRepository: CustomDeckRepository) {

    val allDecks: Flow<List<Deck>> = customDeckRepository.decks.map { custom ->
        (BuiltInDecks.all + custom).sortedBy { it.title }
    }

    fun deckById(id: String): Flow<Deck?> = allDecks.map { decks -> decks.find { it.id == id } }

    suspend fun addCustomDeck(deck: Deck) = customDeckRepository.addDeck(deck)

    suspend fun deleteCustomDeck(id: String) = customDeckRepository.deleteDeck(id)
}
