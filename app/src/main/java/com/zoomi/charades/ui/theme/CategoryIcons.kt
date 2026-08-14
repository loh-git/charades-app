package com.zoomi.charades.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Theaters
import androidx.compose.ui.graphics.vector.ImageVector
import com.zoomi.charades.data.Category

// Vector-icon equivalent of each category, for built-in deck icon boxes (design reference uses
// real vector icons, not emoji). Custom decks keep their user-picked emoji from CreateCustomDeckScreen's
// emoji swatches — there's no vector icon to infer for arbitrary user-authored decks.
val Category.iconVector: ImageVector
    get() = when (this) {
        Category.MOVIES -> Icons.Filled.Theaters
        Category.ANIMALS -> Icons.Filled.Pets
        Category.MUSIC -> Icons.Filled.MusicNote
        Category.ACTIONS -> Icons.AutoMirrored.Filled.DirectionsRun
        Category.FAMOUS_PEOPLE -> Icons.Filled.Person
        Category.RANDOM_OBJECTS -> Icons.Filled.Inventory2
    }
