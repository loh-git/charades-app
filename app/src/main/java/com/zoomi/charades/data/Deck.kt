package com.zoomi.charades.data

import kotlinx.serialization.Serializable

@Serializable
data class Deck(
    val id: String,
    val title: String,
    val category: Category,
    val icon: String,
    val shortDescription: String,
    val howToPlay: String,
    val words: List<String>,
    val isCustom: Boolean = false,
)
