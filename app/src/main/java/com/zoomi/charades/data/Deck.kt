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
    // Set when the deck's creator typed a category name instead of picking a built-in Category —
    // takes precedence over `category` for display. `category` stays a required enum value even
    // then (defaulted to RANDOM_OBJECTS) so existing category-keyed logic (swatchColor, filtering)
    // doesn't need an exhaustive-when case for "no category".
    val customCategoryName: String? = null,
)
