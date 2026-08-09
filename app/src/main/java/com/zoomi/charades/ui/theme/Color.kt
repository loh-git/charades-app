package com.zoomi.charades.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.zoomi.charades.data.Category

val BackgroundDark = Color(0xFF0A0E1A)
val SurfaceDark = Color(0xFF131826)
val SurfaceVariantDark = Color(0xFF1A2035)
val OutlineDark = Color(0xFF262D42)

val OrangeAccent = Color(0xFFFF9500)
val PinkAccent = Color(0xFFFF3D71)

val CorrectGreen = Color(0xFF2ECC71)
val PassOrange = OrangeAccent
val IncorrectRed = Color(0xFFE53935)

val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFF9CA3AF)

val AccentGradient = Brush.horizontalGradient(listOf(OrangeAccent, PinkAccent))

// Light theme
val BackgroundLight = Color(0xFFF7F7FA)
val SurfaceLight = Color(0xFFFFFFFF)
val SurfaceVariantLight = Color(0xFFF0F1F5)
val OutlineLight = Color(0xFFE1E3EA)
val TextPrimaryLight = Color(0xFF14171F)
val TextSecondaryLight = Color(0xFF5B6272)
val OnPrimaryLight = Color(0xFF1A1A1A)

// Pop Art theme
val BackgroundPopArt = Color(0xFFFFF8E7)
val SurfacePopArt = Color(0xFFFFFFFF)
val SurfaceVariantPopArt = Color(0xFFFFF3CC)
val OutlinePopArt = Color(0xFF14171A)
val TextPrimaryPopArt = Color(0xFF14171A)
val TextSecondaryPopArt = Color(0xFF3A3A3A)
val PopArtRed = Color(0xFFE8291C)
val PopArtBlue = Color(0xFF1E5FFF)
val PopArtYellow = Color(0xFFFFD400)

val PopArtAccentGradient = Brush.horizontalGradient(listOf(PopArtRed, PopArtYellow))

// Test theme — a bolder, more playful "toy app" look: vivid saturated cards, thick gold
// borders, bigger icons. See ui/theme/ExtendedColors.kt for the border/icon-size tokens that
// go with this palette. The four neutrals below form a deliberate light-to-dark tonal scale
// (Surface > Background > SurfaceVariant > Outline) so nested elements are always visually
// distinct from whatever they sit on — SurfaceVariant previously matched Surface exactly
// (both pure white), making any surfaceVariant-backed box on Test theme blend invisibly into
// its parent card.
val BackgroundTest = Color(0xFFEDEFFB)
val SurfaceTest = Color(0xFFFFFFFF)
val SurfaceVariantTest = Color(0xFFDDE1FA)
val OutlineTest = Color(0xFFAEB4F0)
val TextPrimaryTest = Color(0xFF201F3D)
val TextSecondaryTest = Color(0xFF5B5B7A)
val TestBlue = Color(0xFF3D5AFE)
val TestPurple = Color(0xFF8B2FE0)
val TestGreen = Color(0xFF00C853)
val TestBorderGold = Color(0xFFFFC700)

val TestAccentGradient = Brush.horizontalGradient(listOf(TestBlue, TestPurple))

private val MoviesSwatch = OrangeAccent
private val AnimalsSwatch = Color(0xFF22C55E)
private val MusicSwatch = Color(0xFFEC4899)
private val ActionsSwatch = Color(0xFF3B82F6)
private val FamousPeopleSwatch = Color(0xFFF59E0B)
private val RandomObjectsSwatch = Color(0xFF06B6D4)

val Category.swatchColor: Color
    get() = when (this) {
        Category.MOVIES -> MoviesSwatch
        Category.ANIMALS -> AnimalsSwatch
        Category.MUSIC -> MusicSwatch
        Category.ACTIONS -> ActionsSwatch
        Category.FAMOUS_PEOPLE -> FamousPeopleSwatch
        Category.RANDOM_OBJECTS -> RandomObjectsSwatch
    }
