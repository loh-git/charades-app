package com.zoomi.charades.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zoomi.charades.data.AppTheme

data class ExtendedColors(
    val accentGradient: Brush,
    val cardBorderWidth: Dp = 0.dp,
    val cardBorderColor: Color = Color.Transparent,
    val deckIconSize: Dp = 48.dp,
    val cardCornerRadius: Dp = 16.dp,
    val cardElevation: Dp = 0.dp,
    val useCategoryTintedCardBackground: Boolean = false,
    val showHeaderTitle: Boolean = true,
    val useBottomNav: Boolean = false,
    val playfulAnimations: Boolean = false,
)

private val DefaultExtendedColors = ExtendedColors(accentGradient = AccentGradient)
private val LightExtendedColors = ExtendedColors(accentGradient = AccentGradient)
private val PopArtExtendedColors = ExtendedColors(accentGradient = PopArtAccentGradient)
private val TestExtendedColors = ExtendedColors(
    accentGradient = TestAccentGradient,
    cardBorderWidth = 3.dp,
    cardBorderColor = TestBorderGold,
    deckIconSize = 64.dp,
    cardCornerRadius = 24.dp,
    cardElevation = 6.dp,
    useCategoryTintedCardBackground = true,
    showHeaderTitle = false,
    useBottomNav = true,
    playfulAnimations = true,
)

val LocalExtendedColors = staticCompositionLocalOf { DefaultExtendedColors }

internal fun extendedColorsFor(theme: AppTheme): ExtendedColors = when (theme) {
    AppTheme.DEFAULT -> DefaultExtendedColors
    AppTheme.LIGHT -> LightExtendedColors
    AppTheme.POP_ART -> PopArtExtendedColors
    AppTheme.TEST -> TestExtendedColors
}
