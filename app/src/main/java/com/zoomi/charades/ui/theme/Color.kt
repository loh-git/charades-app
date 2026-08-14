package com.zoomi.charades.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.zoomi.charades.data.AppTheme
import com.zoomi.charades.data.Category

// "Sunset Arcade" — the app's default, first-launch theme. Values below are taken directly from
// the designer's spec (Tailwind tokens: bg-slate-950/900/800/700, amber-500, orange-600,
// emerald-400, rose-400/500) rather than a visual read of screenshots — exact, not a guess.
val BackgroundDark = Color(0xFF020617) // explicit designer spec
val SurfaceDark = Color(0xFF0F172A) // slate-900
val SurfaceVariantDark = Color(0xFF0E1529) // slate-800#
val OutlineDark = Color(0xFF334155) // slate-700 — kept a step lighter than SurfaceVariant so borders stay visible against it

val OrangeAccent = Color(0xFFF59E0B) // amber-500
val SunsetGradientEnd = Color(0xFFEA580C) // orange-600 — Sunset Arcade's gradient partner specifically (was PinkAccent)
val PinkAccent = Color(0xFFFF3D71)

val CorrectGreen = Color(0xFF34D399) // emerald-400
val IncorrectRed = Color(0xFFFB7185) // rose-400 — also the "pass" color (was a plain saturated red)
val PassRose = IncorrectRed

val TextPrimary = Color(0xFFF1F5F9) // slate-100
val TextSecondary = Color(0xFF94A3B8) // slate-400
val CategoryMutedGray = Color(0xFF90A1B9) // unselected category-pill icon/text/border color, per explicit spec

val AccentGradient = Brush.horizontalGradient(listOf(OrangeAccent, SunsetGradientEnd))

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

// Preview swatches for the Settings screen's theme picker card — each theme's dominant
// background and accent, used for the little avatar/color-chip preview, not the live theme itself.
val AppTheme.previewBackground: Color
    get() = when (this) {
        AppTheme.DEFAULT -> SurfaceDark
        AppTheme.LIGHT -> BackgroundLight
        AppTheme.POP_ART -> BackgroundPopArt
        AppTheme.TEST -> BackgroundTest
        AppTheme.STUDIO_MINIMALIST -> BackgroundLight
    }

val AppTheme.previewAccent: Color
    get() = when (this) {
        AppTheme.DEFAULT -> OrangeAccent
        AppTheme.LIGHT -> OrangeAccent
        AppTheme.POP_ART -> PopArtRed
        AppTheme.TEST -> TestBlue
        AppTheme.STUDIO_MINIMALIST -> OrangeAccent
    }
