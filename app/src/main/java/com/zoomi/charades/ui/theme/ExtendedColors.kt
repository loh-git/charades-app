package com.zoomi.charades.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
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
    val cardBackgroundOverride: Color? = null, // when set, replaces colorScheme.surfaceVariant as the deck card fill
    val showHeaderTitle: Boolean = true,
    val useBottomNav: Boolean = false,
    val playfulAnimations: Boolean = false,

    // Modal/card chrome shared by Settings, Stats, Create Custom Deck, Deck Detail and the
    // in-game Pause dialog. Defaults reproduce Sunset Arcade's original hardcoded slate-900/800
    // values exactly, so Default/Light/PopArt/Test are visually unaffected by these fields
    // existing — only a theme that explicitly overrides them (Matrix Terminal) looks different.
    val modalSurface: Color = Color(0xFF0F172A), // slate-900
    val modalBorder: Color = Color(0xFF1E293B), // slate-800
    val dropdownSurface: Color = Color(0xFF1E293B), // the theme dropdown's own box fill (was reusing modalBorder's value)
    val chromeSurface: Color = Color(0xFF1E293B), // secondary dark box fill (e.g. in-game pause button)
    val chromeBorder: Color = Color.Transparent, // paired border for chromeSurface boxes
    val inputSurface: Color = Color(0xCC1E293B), // slate-800/80, text-field container fill
    val inputPlaceholder: Color = Color(0xFF64748B), // slate-500
    val textStrong: Color = Color(0xFFF8FAFC), // slate-50, headline text on dark modal chrome
    val accentBright: Color = Color(0xFFFBBF24), // amber-400, bright icon/link accent
    val iconAccentGold: Color = Color(0xFFFFB900), // deck badges, "Charades" title highlight, info icons
    val dividerFaint: Color = Color(0x1AFFFFFF), // white/10 hairline dividers
    val recessedContainerBackground: Color = Color(0x33000000), // e.g. word-list builder box
    val toggleThumb: Color = Color.White, // Settings screen's toggle-switch knob

    // The app's one neutral/secondary button + choice-pill palette (was the standalone
    // NeutralButtonColors object) and modal close (X) button.
    val neutralButtonBackground: Color = Color(0xFF182234),
    val neutralButtonBackgroundPressed: Color = Color(0xFF2E3F58),
    val neutralButtonText: Color = Color(0xFFCAD5E2),
    val neutralButtonBorderWidth: Dp = 0.dp,
    val neutralButtonBorderColor: Color = Color.Transparent,
    val pillBackground: Color = Color(0xFF182234),
    val pillBorder: Color = Color(0xFF212D42),
    val pillText: Color = Color(0xFF90A1B9),
    val closeIconBackground: Color = Color(0xFF0F172A),
    val closeIconBackgroundPressed: Color = Color(0xFF243044),
    val closeIconTint: Color = Color(0xFF94A3B8),

    // Deck card's category icon badge + category-name pill (a bespoke amber-brown, not just a
    // translucent tint of primary).
    val deckBadgeBackground: Color = Color(0xFF3E2F20),
    val deckBadgeBorder: Color = Color(0xFF6C4818),

    // Deck list top bar's icon buttons.
    val iconButtonBackground: Color = Color(0xFF1D293D),
    val iconButtonBorder: Color = Color(0xFF334155),
    val iconButtonContent: Color = Color(0xFFE2E8F0),
    val primarySolidIconButtonBackground: Color = Color(0xFFFE9A00), // settings gear button fill
    val shuffleIconTint: Color = Color(0xFFD29704),

    // CRT/neon glow — Color.Transparent (with 0.dp elevation) disables it entirely, which is the
    // default for every theme except Matrix Terminal. Deck cards get their own, dimmer glow
    // (kept separate from the pill/icon/countdown glow above) since a full-strength ambient glow
    // read as too strong against a whole card's edge.
    val glowColor: Color = Color.Transparent,
    val glowElevation: Dp = 0.dp,
    val cardGlowColor: Color = Color.Transparent,
    val cardGlowElevation: Dp = 0.dp,
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

// Matrix Terminal — every chrome token above re-pointed at pure black + neon phosphor green.
private val MatrixExtendedColors = ExtendedColors(
    accentGradient = MatrixAccentGradient,
    cardBorderWidth = 1.dp,
    cardBorderColor = MatrixBorderGreen,
    modalSurface = MatrixBackground,
    modalBorder = MatrixBorderGreen,
    dropdownSurface = MatrixBackground,
    cardBackgroundOverride = MatrixBackground,
    chromeSurface = MatrixBackground,
    chromeBorder = MatrixBorderGreen,
    inputSurface = MatrixBackground,
    inputPlaceholder = MatrixPlaceholderGreen,
    textStrong = MatrixBrightGreen,
    accentBright = MatrixBrightGreen,
    iconAccentGold = MatrixBrightGreen,
    dividerFaint = MatrixBorderGreen,
    recessedContainerBackground = MatrixContainerGreen,
    neutralButtonBackground = MatrixBackground,
    neutralButtonBackgroundPressed = Color(0xFF052E16),
    neutralButtonText = MatrixBrightGreen,
    neutralButtonBorderWidth = 1.dp,
    neutralButtonBorderColor = MatrixBorderGreen,
    pillBackground = MatrixBackground,
    pillBorder = MatrixBorderGreenDim,
    pillText = MatrixMutedGreen,
    closeIconBackground = MatrixBackground,
    closeIconBackgroundPressed = Color(0xFF052E16),
    closeIconTint = MatrixBrightGreen,
    deckBadgeBackground = MatrixContainerGreen,
    deckBadgeBorder = MatrixBorderGreen,
    iconButtonBackground = MatrixBackground,
    iconButtonBorder = MatrixBorderGreen,
    iconButtonContent = MatrixBrightGreen,
    primarySolidIconButtonBackground = MatrixPrimaryGreen,
    shuffleIconTint = MatrixBrightGreen,
    glowColor = MatrixGlow.copy(alpha = 0.5f),
    glowElevation = 12.dp,
    cardGlowColor = MatrixGlow.copy(alpha = 0.25f),
    cardGlowElevation = 6.dp,
)

val LocalExtendedColors = staticCompositionLocalOf { DefaultExtendedColors }

internal fun extendedColorsFor(theme: AppTheme): ExtendedColors = when (theme) {
    AppTheme.DEFAULT -> DefaultExtendedColors
    AppTheme.MATRIX_TERMINAL -> MatrixExtendedColors
    AppTheme.POP_ART -> PopArtExtendedColors
    AppTheme.TEST -> TestExtendedColors
    AppTheme.STUDIO_MINIMALIST -> LightExtendedColors
}

// A no-op on every theme except Matrix Terminal (glowColor is Color.Transparent elsewhere) — a
// colored ambient/spot shadow standing in for CSS's box-shadow-based neon glow. Defaults to the
// standard pill/icon/countdown glow; pass color/elevation explicitly for a different strength
// (e.g. deck cards' dimmer cardGlowColor/cardGlowElevation).
fun Modifier.themedGlow(
    extended: ExtendedColors,
    shape: Shape,
    color: Color = extended.glowColor,
    elevation: Dp = extended.glowElevation,
): Modifier =
    if (color == Color.Transparent) {
        this
    } else {
        this.shadow(elevation = elevation, shape = shape, ambientColor = color, spotColor = color)
    }
