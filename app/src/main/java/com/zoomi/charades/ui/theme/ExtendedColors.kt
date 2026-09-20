package com.zoomi.charades.ui.theme

import androidx.compose.foundation.background
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.translate
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
    val modalBorderWidth: Dp = 1.dp,
    val dropdownSurface: Color = Color(0xFF1E293B), // the theme dropdown's own box fill (was reusing modalBorder's value)
    val chromeSurface: Color = Color(0xFF1E293B), // secondary dark box fill (e.g. in-game pause button)
    val chromeBorder: Color = Color.Transparent, // paired border for chromeSurface boxes
    val chromeBorderWidth: Dp = 1.dp,
    val inputSurface: Color = Color(0xCC1E293B), // slate-800/80, text-field container fill
    val inputPlaceholder: Color = Color(0xFF64748B), // slate-500
    val textStrong: Color = Color(0xFFF8FAFC), // slate-50, headline text on dark modal chrome
    val accentBright: Color = Color(0xFFFBBF24), // amber-400, bright icon/link accent
    val titleAccentColor: Color = Color(0xFFFFB900), // "Charades" title highlight
    val dividerFaint: Color = Color(0x1AFFFFFF), // white/10 hairline dividers
    val recessedContainerBackground: Color = Color(0x33000000), // e.g. word-list builder box
    val toggleThumb: Color = Color.White, // Settings screen's toggle-switch knob

    // Settings modal's own inner content padding (gap between the modal's border and the
    // settings rows inside it) — 24.dp everywhere except a theme that wants it tighter.
    val settingsContentPadding: Dp = 24.dp,
    // Stats modal's own max width — 560.dp everywhere (matching every other modal) except a
    // theme that wants it to match some other modal's width instead (e.g. the Shuffle dialog's).
    val statsModalMaxWidth: Dp = 560.dp,

    // In-game manual Pass/Correct tap-fallback buttons — always red/green (accentColor, passed
    // in by the caller) but the surrounding chrome (fill, border weight) is themeable so it fits
    // each theme's chrome instead of always using Sunset Arcade's hardcoded panel colors. null
    // background keeps the caller's own per-state color (today's behavior).
    val manualActionButtonBackground: Color? = null,
    val manualActionButtonBorderWidth: Dp = 1.dp,

    // The app's one neutral/secondary button + choice-pill palette (was the standalone
    // NeutralButtonColors object) and modal close (X) button.
    val neutralButtonBackground: Color = Color(0xFF182234),
    val neutralButtonBackgroundPressed: Color = Color(0xFF2E3F58),
    val neutralButtonText: Color = Color(0xFFCAD5E2),
    val neutralButtonBorderWidth: Dp = 0.dp,
    val neutralButtonBorderColor: Color = Color.Transparent,
    val pillBackground: Color = Color(0xFF182234),
    val pillBorder: Color = Color(0xFF212D42),
    val pillBorderWidth: Dp = 1.dp,
    val pillText: Color = Color(0xFF90A1B9),
    val closeIconBackground: Color = Color(0xFF0F172A),
    val closeIconBackgroundPressed: Color = Color(0xFF243044),
    val closeIconTint: Color = Color(0xFF94A3B8),

    // Deck card's category-name pill (a bespoke amber-brown, not just a translucent tint of
    // primary) — also reused for Deck Detail's "how to play" info icon, another "icon/text inside
    // an accent-colored chip" role.
    val deckBadgeBackground: Color = Color(0xFF3E2F20),
    val deckBadgeBorder: Color = Color(0xFF6C4818),
    val deckBadgeTint: Color = Color(0xFFFFB900),

    // Generic "icon in a colored box" badge — the deck card's icon emblem and the hero trophy
    // icon (RoundSummary/FinalStandings' ScreenHeaderIcon). Defaults match the deck badge above
    // since they were the same values before this got its own token.
    val iconBadgeBackground: Color = Color(0xFF3E2F20),
    val iconBadgeBorder: Color = Color(0xFF6C4818),
    val iconBadgeTint: Color = Color(0xFFFFB900),
    // Shared "small" border width for every compact bordered chip/button — icon badges, the deck
    // card's category-name pill, and the deck list's header icon buttons (Shuffle/Stats/Settings).
    val iconBadgeBorderWidth: Dp = 1.dp,
    // ScreenHeaderIcon's (RoundSummary/FinalStandings trophy) own box — null falls back to its
    // current computed primary.copy(alpha = 0.2f/0.3f) look rather than the opaque iconBadge* set.
    val headerIconBackground: Color? = null,
    val headerIconBorder: Color? = null,
    val headerIconTint: Color? = null,
    // Deck card's "Select Deck →" action row — null falls back to colorScheme.primary.
    val deckSelectActionColor: Color? = null,
    // Settings screen's theme-preview section border — null falls back to
    // colorScheme.primary.copy(alpha = 0.3f); overridden by a theme that wants a different color.
    val themePreviewBorderColor: Color? = null,
    // Generic icon-badge gradient overlay (e.g. Cyberpunk's pink-to-cyan deck emblem box) —
    // painted over iconBadgeBackground when set; null everywhere except a theme that wants one.
    val iconBadgeGradient: Brush? = null,

    // Deck Detail's description text and "how to play" info box — null falls back to today's
    // colorScheme.onSurfaceVariant/onSurface text and colorScheme.surfaceVariant background.
    val deckInfoTextColor: Color? = null,
    val deckInfoBackground: Color? = null,

    // Deck list top bar's icon buttons.
    val iconButtonBackground: Color = Color(0xFF1D293D),
    val iconButtonBorder: Color = Color(0xFF334155),
    val iconButtonContent: Color = Color(0xFFE2E8F0),
    val primarySolidIconButtonBackground: Color = Color(0xFFFE9A00), // settings gear button fill
    val primarySolidIconButtonBorder: Color? = null, // null = no border (current behavior)
    val primarySolidIconButtonContent: Color? = null, // null = fall back to colorScheme.onPrimary
    val shuffleIconTint: Color = Color(0xFFD29704),
    // null = fall back to primary.copy(alpha = 0.2f/0.3f), the current computed look
    val shuffleIconBackground: Color? = null,
    val shuffleIconBorder: Color? = null,
    // Per-button ambient glow for the deck list's three header icon buttons — null disables it.
    val shuffleGlowColor: Color? = null,
    val statsGlowColor: Color? = null,
    val settingsGlowColor: Color? = null,
    val headerButtonGlowElevation: Dp = 0.dp,
    // When set, paints over primarySolidIconButtonBackground on the settings header button —
    // the one header button this theme renders as a gradient rather than a flat fill.
    val settingsButtonGradient: Brush? = null,

    // Category filter pills' selected state. null = fall back to colorScheme.primary/onPrimary,
    // today's behavior; a theme with its own distinct "active" accent (separate from the primary
    // CTA color) overrides these instead of colorScheme.primary itself, so buttons elsewhere
    // stay on-brand. Selected pills draw no border by default, matching today's look — a theme
    // that wants one on both selected and unselected pills sets selectedPillBorderWidth too.
    val selectedPillBackground: Color? = null,
    val selectedPillContent: Color? = null,
    val selectedPillBorderWidth: Dp = 0.dp,
    val selectedPillBorderColor: Color = Color.Transparent,

    // OptionPill's two special-cased roles (round-duration timer pills and Deck Detail's
    // Classic/Party mode pills) — both null/Transparent everywhere except a theme that wants a
    // distinct look for one of these specific roles instead of the generic selected-pill fill.
    val timerOptionGradient: Brush? = null,
    val modeOptionGlowColor: Color = Color.Transparent,
    val modeOptionGlowElevation: Dp = 0.dp,

    // A visible ink border around primary CTA buttons (Start Game, Play Again, Save Deck, Create
    // Custom Deck banner, etc.) — 0dp/Transparent everywhere except a theme that wants one.
    val primaryButtonBorderWidth: Dp = 0.dp,
    val primaryButtonBorderColor: Color = Color.Transparent,
    // When set, paints every primary CTA button with this gradient instead of a solid
    // colorScheme.primary fill (the button's containerColor is set to Transparent so it shows
    // through) — null everywhere except a theme whose buttons are explicitly gradient-filled.
    val primaryButtonGradient: Brush? = null,
    val primaryButtonGlowColor: Color = Color.Transparent,
    val primaryButtonGlowElevation: Dp = 0.dp,

    // The deck list's "+ Create Custom Deck" banner button specifically — null falls back to
    // primaryButtonGradient/primaryButtonGlowColor above (today's behavior), overridden only by a
    // theme that wants this one CTA to stand out with its own distinct gradient/glow.
    val createDeckButtonGradient: Brush? = null,
    val createDeckButtonGlowColor: Color = Color.Transparent,
    val createDeckButtonGlowElevation: Dp = 0.dp,

    // CRT/neon glow — Color.Transparent (with 0.dp elevation) disables it entirely, which is the
    // default for every theme except Matrix Terminal. Deck cards get their own, dimmer glow
    // (kept separate from the pill/icon/countdown glow above) since a full-strength ambient glow
    // read as too strong against a whole card's edge.
    val glowColor: Color = Color.Transparent,
    val glowElevation: Dp = 0.dp,
    // Icon-badge glow (deck card's icon emblem, the hero trophy icon) — kept separate from the
    // pill/countdown glow above since it's a different color for Cyberpunk Neon (pink vs. cyan).
    val iconBadgeGlowColor: Color = Color.Transparent,
    val iconBadgeGlowElevation: Dp = 0.dp,
    val cardGlowColor: Color = Color.Transparent,
    val cardGlowElevation: Dp = 0.dp,

    // Neo-brutalist hard-edged offset "sticker" shadow (Pop Art) — a flat, unblurred duplicate of
    // the shape drawn behind it at a fixed offset, as opposed to glowColor's blurred ambient
    // shadow. Color.Transparent disables it, the default everywhere except Pop Art.
    val hardShadowColor: Color = Color.Transparent,
    val hardShadowOffsetLarge: Dp = 0.dp, // cards, primary buttons, modals
    val hardShadowOffsetSmall: Dp = 0.dp, // pills, icon badges, header buttons
)

private val DefaultExtendedColors = ExtendedColors(accentGradient = AccentGradient)
// "Colourful Pop" — Neo-Brutalist comic-book look: bold ink borders, hard offset "sticker"
// shadows (no blur), pastel accents on a white-card/butter-yellow-canvas backdrop.
private val PopArtExtendedColors = ExtendedColors(
    accentGradient = PopArtAccentGradient,
    cardBorderWidth = 3.dp,
    cardBorderColor = OutlinePopArt,
    cardBackgroundOverride = SurfacePopArt,
    modalSurface = SurfacePopArt,
    modalBorder = OutlinePopArt,
    modalBorderWidth = 3.dp,
    dropdownSurface = SurfacePopArt,
    chromeSurface = SurfacePopArt,
    chromeBorder = OutlinePopArt,
    chromeBorderWidth = 2.dp,
    inputSurface = SurfacePopArt,
    textStrong = TextPrimaryPopArt,
    accentBright = PopArtPink600,
    titleAccentColor = PopArtPink600,
    dividerFaint = OutlinePopArt.copy(alpha = 0.15f),
    recessedContainerBackground = OutlinePopArt.copy(alpha = 0.05f),
    neutralButtonBackground = SurfacePopArt,
    neutralButtonBackgroundPressed = Color(0xFFF1F5F9), // slate-100, per spec's hover:bg-slate-100
    neutralButtonText = TextPrimaryPopArt,
    neutralButtonBorderWidth = 2.dp,
    neutralButtonBorderColor = OutlinePopArt,
    pillBackground = SurfacePopArt,
    pillBorder = OutlinePopArt,
    pillBorderWidth = 2.dp,
    pillText = TextPrimaryPopArt,
    closeIconBackground = SurfacePopArt,
    closeIconBackgroundPressed = Color(0xFFF1F5F9),
    closeIconTint = TextPrimaryPopArt,
    deckBadgeBackground = PopArtSky300,
    deckBadgeBorder = OutlinePopArt,
    deckBadgeTint = TextPrimaryPopArt,
    iconBadgeBackground = PopArtAmber300,
    iconBadgeBorder = OutlinePopArt,
    iconBadgeTint = TextPrimaryPopArt,
    iconBadgeBorderWidth = 2.dp,
    headerIconBackground = PopArtAmber300,
    headerIconBorder = OutlinePopArt,
    headerIconTint = TextPrimaryPopArt,
    deckSelectActionColor = TextPrimaryPopArt,
    themePreviewBorderColor = OutlinePopArt,
    iconButtonBackground = PopArtEmerald300, // stats header button
    iconButtonBorder = OutlinePopArt,
    iconButtonContent = TextPrimaryPopArt,
    primarySolidIconButtonBackground = PopArtPink400, // settings header button
    primarySolidIconButtonBorder = OutlinePopArt,
    shuffleIconTint = TextPrimaryPopArt,
    shuffleIconBackground = PopArtSky300,
    shuffleIconBorder = OutlinePopArt,
    selectedPillBackground = PopArtPink400,
    selectedPillContent = TextPrimaryPopArt,
    selectedPillBorderWidth = 2.dp,
    selectedPillBorderColor = OutlinePopArt,
    primaryButtonBorderWidth = 3.dp,
    primaryButtonBorderColor = OutlinePopArt,
    hardShadowColor = OutlinePopArt,
    // Nudged in from the original 4.dp/2.dp offsets — the sticker shadow sat too far down-right;
    // pulling it in slightly reads as shifted up and to the left relative to where it sat before.
    hardShadowOffsetLarge = 3.dp,
    hardShadowOffsetSmall = 1.dp,
    // Keeps the manual Pass/Correct buttons' red/green accent but swaps Sunset Arcade's dark
    // purple/teal panel fills for the theme's white card surface + a bolder ink border, matching
    // every other Pop Art button instead of clashing with the comic-book look.
    manualActionButtonBackground = SurfacePopArt,
    manualActionButtonBorderWidth = 3.dp,
)
// Cyberpunk Neon — dark glass panels, fine cyan wireframe borders, dual-tone magenta/cyan glow,
// pink-to-rose gradient CTAs. Uses themedGlow (blurred ambient glow), not hardShadow.
private val CyberpunkExtendedColors = ExtendedColors(
    accentGradient = CyberAccentGradient,
    cardBorderWidth = 1.dp,
    cardBorderColor = CyberCyan500.copy(alpha = 0.4f),
    cardBackgroundOverride = CyberCardSurface, // deck cards keep their deliberate translucent "glass" look
    // Modals/overlays use the opaque solid surface, not the translucent glass-card one — every
    // other theme's overlaid screens are fully opaque, and Cyberpunk's modals should match.
    modalSurface = CyberSurfaceSolid,
    // The same pink used by the deck card's "Select Deck →" action text (deckSelectActionColor
    // below), per an explicit request to match modal borders to it instead of the cyan wireframe.
    modalBorder = CyberPink400,
    modalBorderWidth = 1.dp,
    dropdownSurface = CyberSurfaceSolid,
    chromeSurface = CyberCardSurface,
    chromeBorder = CyberCyan500.copy(alpha = 0.4f),
    chromeBorderWidth = 1.dp,
    inputSurface = CyberSurfaceSolid,
    textStrong = CyberCyan200,
    accentBright = CyberPink400,
    titleAccentColor = Color(0xFFCA5197), // explicit spec color for the "Charades" header highlight
    dividerFaint = CyberCyan500.copy(alpha = 0.2f),
    recessedContainerBackground = Color(0x1AFFFFFF), // white/10 — a black tint would vanish on this already-dark canvas
    neutralButtonBackground = CyberSurfaceSolid,
    neutralButtonBackgroundPressed = CyberSlate800,
    neutralButtonText = CyberSlate300,
    neutralButtonBorderWidth = 1.dp,
    neutralButtonBorderColor = CyberSlate700,
    pillBackground = CyberPillSurface,
    pillBorder = CyberSlate800,
    pillBorderWidth = 1.dp,
    pillText = CyberSlate400,
    closeIconBackground = CyberSurfaceSolid,
    closeIconBackgroundPressed = CyberSlate800,
    closeIconTint = CyberSlate300,
    deckBadgeBackground = Color(0x99500724), // pink-950 @ 60%
    deckBadgeBorder = CyberPink500.copy(alpha = 0.4f),
    deckBadgeTint = CyberPink300,
    iconBadgeBackground = Color.Transparent, // fully replaced by iconBadgeGradient below
    iconBadgeGradient = Brush.linearGradient(listOf(CyberPink500.copy(alpha = 0.2f), CyberCyan500.copy(alpha = 0.2f))),
    iconBadgeBorder = CyberCyan400.copy(alpha = 0.5f),
    iconBadgeTint = CyberCyan300,
    iconBadgeGlowColor = CyberPink500.copy(alpha = 0.4f),
    iconBadgeGlowElevation = 20.dp,
    headerIconBackground = CyberPurple500.copy(alpha = 0.18f),
    headerIconBorder = CyberCyan400.copy(alpha = 0.5f),
    headerIconTint = CyberCyan300,
    deckSelectActionColor = CyberPink400,
    deckInfoTextColor = Color(0xFF53EAFD),
    deckInfoBackground = Color(0xFF53EAFD).copy(alpha = 0.08f),
    iconButtonBackground = Color(0x99500724), // stats header button — pink-950 @ 60%
    iconButtonBorder = CyberPink500.copy(alpha = 0.4f),
    iconButtonContent = CyberPink300,
    primarySolidIconButtonBackground = CyberPink500, // settings header button fallback fill
    primarySolidIconButtonContent = Color.White,
    settingsButtonGradient = CyberButtonGradient,
    shuffleIconTint = CyberCyan300,
    shuffleIconBackground = Color(0x99083344), // cyan-950 @ 60%
    shuffleIconBorder = CyberCyan500.copy(alpha = 0.4f),
    shuffleGlowColor = CyberCyan500.copy(alpha = 0.2f),
    statsGlowColor = CyberPink500.copy(alpha = 0.2f),
    settingsGlowColor = CyberPink500.copy(alpha = 0.4f),
    headerButtonGlowElevation = 10.dp,
    selectedPillBorderWidth = 0.dp,
    timerOptionGradient = Brush.horizontalGradient(listOf(Color(0xFFF63196), Color(0xFFED0044))),
    modeOptionGlowColor = CyberCyan400.copy(alpha = 0.5f),
    modeOptionGlowElevation = 30.dp,
    primaryButtonGradient = CyberButtonGradient,
    primaryButtonGlowColor = CyberPink500.copy(alpha = 0.4f),
    primaryButtonGlowElevation = 20.dp,
    createDeckButtonGradient = CyberAccentGradient, // the tri-color pink-purple-cyan gradient, not the CTA's pink-rose one
    createDeckButtonGlowColor = CyberPink500.copy(alpha = 0.3f),
    createDeckButtonGlowElevation = 20.dp,
    glowColor = CyberCyan500.copy(alpha = 0.4f),
    glowElevation = 15.dp,
    // Pink glow border, shared by deck cards and every modal (both call this same field).
    cardGlowColor = CyberPink500.copy(alpha = 0.4f),
    cardGlowElevation = 20.dp,
)

// Matrix Terminal — every chrome token above re-pointed at pure black + neon phosphor green.
private val MatrixExtendedColors = ExtendedColors(
    accentGradient = MatrixAccentGradient,
    cardBorderWidth = 1.dp,
    cardBorderColor = MatrixBorderGreen,
    modalSurface = MatrixBackground,
    modalBorder = MatrixBorderGreen,
    // The modal's own outer width matches every other theme's (no settingsHorizontalPadding
    // override) — only its inner content padding is pulled way in from the shared 24.dp default,
    // so just a small gap remains between the settings rows and the modal's own border.
    settingsContentPadding = 8.dp,
    // Matches the Shuffle dialog's fixed 440.dp width instead of the shared 560.dp modal width.
    statsModalMaxWidth = 440.dp,
    dropdownSurface = MatrixBackground,
    cardBackgroundOverride = MatrixBackground,
    chromeSurface = MatrixBackground,
    chromeBorder = MatrixBorderGreen,
    inputSurface = MatrixBackground,
    inputPlaceholder = MatrixPlaceholderGreen,
    textStrong = MatrixBrightGreen,
    accentBright = MatrixBrightGreen,
    titleAccentColor = MatrixBrightGreen,
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
    deckBadgeTint = MatrixBrightGreen,
    iconBadgeBackground = MatrixContainerGreen,
    iconBadgeBorder = MatrixBorderGreen,
    iconBadgeTint = MatrixBrightGreen,
    iconBadgeGlowColor = MatrixGlow.copy(alpha = 0.5f),
    iconBadgeGlowElevation = 12.dp,
    // Matches iconBadge* above — left unset, ScreenHeaderIcon's trophy fell back to a translucent
    // primary.copy(alpha=0.2f) fill, and that translucency let its iconBadgeGlow shadow bleed
    // through as a visibly darker rounded box inside the container. An opaque fill (like every
    // other icon badge in this theme already uses) fully occludes the shadow instead.
    headerIconBackground = MatrixContainerGreen,
    headerIconBorder = MatrixBorderGreen,
    headerIconTint = MatrixBrightGreen,
    iconButtonBackground = MatrixBackground,
    iconButtonBorder = MatrixBorderGreen,
    iconButtonContent = MatrixBrightGreen,
    primarySolidIconButtonBackground = MatrixPrimaryGreen,
    shuffleIconTint = MatrixBrightGreen,
    // A dimmer glow than the general CRT glowColor below — applies to every primary CTA button
    // sharing this theme's primary-green fill (Create Custom Deck, Start Game, Save Deck, Resume,
    // etc.) via primaryButtonColors/themedGlow. The header Settings button is unaffected since it
    // reads its own settingsGlowColor, which this theme leaves unset.
    primaryButtonGlowColor = MatrixGlow.copy(alpha = 0.35f),
    primaryButtonGlowElevation = 8.dp,
    // Keeps the manual Pass/Correct buttons' red/green accent but swaps Sunset Arcade's dark
    // purple/teal panel fills for this theme's black canvas, so the buttons read as bordered
    // cutouts in the CRT terminal chrome instead of clashing with it.
    manualActionButtonBackground = MatrixBackground,
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
    AppTheme.CYBERPUNK_NEON -> CyberpunkExtendedColors
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

// A no-op on every theme except Pop Art (hardShadowColor is Color.Transparent elsewhere) — draws
// a flat, unblurred duplicate of the shape behind the content, offset down-right, standing in for
// CSS's `box-shadow: Npx Npx 0px 0px black` neo-brutalist "sticker" shadow. Pass large = false for
// the smaller offset used by pills/icon badges/header buttons.
fun Modifier.hardShadow(extended: ExtendedColors, shape: Shape, large: Boolean = true): Modifier {
    val offset = if (large) extended.hardShadowOffsetLarge else extended.hardShadowOffsetSmall
    return if (extended.hardShadowColor == Color.Transparent || offset == 0.dp) {
        this
    } else {
        this.drawBehind {
            val offsetPx = offset.toPx()
            translate(left = offsetPx, top = offsetPx) {
                drawOutline(shape.createOutline(size, layoutDirection, this), color = extended.hardShadowColor)
            }
        }
    }
}

// Paints extended.primaryButtonGradient behind a Button's content when set; a no-op otherwise.
// Pair with primaryButtonColors(extended) so the Button's own containerColor doesn't paint over it.
fun Modifier.primaryButtonGradientBackground(extended: ExtendedColors, shape: Shape): Modifier =
    extended.primaryButtonGradient?.let { background(it, shape) } ?: this

// containerColor is Transparent when a gradient is set (so primaryButtonGradientBackground shows
// through), otherwise the theme's normal solid colorScheme.primary/onPrimary fill. Disabled
// colors are kept identical to the enabled ones: Material3's own disabled defaults derive from
// onSurface at low alpha, and that translucency let hardShadow's fully opaque shape (drawn
// directly behind every primary button) bleed straight through as a solid black smear on themes
// like Pop Art. A caller that wants a visibly dimmed disabled state wraps the whole button in
// Modifier.alpha() instead, which fades the shadow along with everything else uniformly.
@Composable
fun primaryButtonColors(extended: ExtendedColors): ButtonColors {
    val container = if (extended.primaryButtonGradient != null) Color.Transparent else MaterialTheme.colorScheme.primary
    val content = if (extended.primaryButtonGradient != null) Color.White else MaterialTheme.colorScheme.onPrimary
    return ButtonDefaults.buttonColors(
        containerColor = container,
        contentColor = content,
        disabledContainerColor = container,
        disabledContentColor = content,
    )
}
