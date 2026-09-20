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

// Pop Art theme — "Colourful Pop", a Neo-Brutalist comic-book look (bold ink outlines, hard
// offset "sticker" shadows, pastel accent colors on a warm butter-yellow canvas), per an explicit
// design spec (Tailwind tokens). Values taken directly from that spec.
val BackgroundPopArt = Color(0xFFFEF3C7) // amber-100 — canvas
val SurfacePopArt = Color(0xFFFFFFFF) // white — card/container surfaces
val OutlinePopArt = Color(0xFF0F172A) // slate-900 — "ink": all text, outlines, hard shadows
val TextPrimaryPopArt = Color(0xFF0F172A) // slate-900
val TextSecondaryPopArt = Color(0xFF334155) // slate-700

val PopArtYellow400 = Color(0xFFFACC15) // primary CTA buttons
val PopArtPink400 = Color(0xFFF472B6) // active tabs, settings button
val PopArtPink600 = Color(0xFFDB2777) // logo highlight, countdown number
val PopArtSky300 = Color(0xFF7DD3FC) // shuffle button, category badges
val PopArtEmerald300 = Color(0xFF6EE7B7) // stats button, correct state
val PopArtRose200 = Color(0xFFFECDD3)
val PopArtRose300 = Color(0xFFFDA4AF) // pass state, error tags
val PopArtAmber300 = Color(0xFFFCD34D) // deck emblem icon box

val PopArtAccentGradient = Brush.horizontalGradient(listOf(PopArtPink600, PopArtPink600)) // solid, not a real gradient — spec calls for solid pink-600 wherever this is used

// Cyberpunk Neon theme (AppTheme.CYBERPUNK_NEON) — a midnight synthwave HUD look: dark glass
// panels, fine cyan wireframe borders, dual-tone magenta/cyan glow, pink-to-cyan gradients on
// primary CTAs. Values taken directly from an explicit design spec (Tailwind tokens).
val CyberBackground = Color(0xFF020617) // slate-950
val CyberSurfaceSolid = Color(0xFF0F172A) // slate-900, opaque — inputs, neutral buttons
val CyberCardSurface = Color(0xE60F172A) // slate-900 @ ~90% — translucent glass card/modal panels
val CyberPillSurface = Color(0xCC0F172A) // slate-900 @ 80% — inactive tab/pill fill
val CyberSlate700 = Color(0xFF334155) // borders on neutral/back buttons
val CyberSlate800 = Color(0xFF1E293B) // inactive tab border
val CyberSlate300 = Color(0xFFCBD5E1) // neutral/back button text
val CyberSlate400 = Color(0xFF94A3B8) // muted secondary text, inactive tab text

val CyberPink300 = Color(0xFFF9A8D4) // muted pink text (category badge, stats button)
val CyberPink400 = Color(0xFFF472B6) // "Charades" logo highlight, select-deck action, accents
val CyberPink500 = Color(0xFFEC4899) // gradient start, glow tint
val CyberRose600 = Color(0xFFE11D48) // gradient end (CTA buttons, settings button)
val CyberPurple500 = Color(0xFFA855F7) // tri-color gradient mid-stop

val CyberCyan200 = Color(0xFFCFFAFE) // bright display text ("Ultimate", headline text)
val CyberCyan300 = Color(0xFF67E8F9) // secondary highlights/icons, shuffle button text
val CyberCyan400 = Color(0xFF22D3EE) // countdown/active-word glow color
val CyberCyan500 = Color(0xFF06B6D4) // active pill fill, framing border base color

val CyberRose400 = Color(0xFFFB7185) // pass/incorrect feedback text (matches IncorrectRed)
val CyberRose950 = Color(0xFF4C0519) // pass feedback container

val CyberAccentGradient = Brush.horizontalGradient(listOf(CyberPink500, CyberPurple500, CyberCyan500))
val CyberButtonGradient = Brush.horizontalGradient(listOf(CyberPink500, CyberRose600))

// Matrix Terminal theme — a black CRT-terminal look with neon phosphor green, per an explicit
// design spec (Tailwind green-400/500/600/950 tokens, monospace type, glowing borders). Values
// taken directly from that spec rather than eyeballed.
val MatrixBackground = Color(0xFF000000)
val MatrixBrightGreen = Color(0xFF4ADE80) // green-400 — headings, active/bright text and icons
val MatrixPrimaryGreen = Color(0xFF22C55E) // green-500 — solid buttons, active tab fills
val MatrixMutedGreen = Color(0xFF16A34A) // green-600 — secondary text, muted/inactive items
val MatrixPlaceholderGreen = Color(0xFF166534) // green-800 — input placeholder text
val MatrixContainerGreen = Color(0x99052E16) // green-950 @ ~60% — deep terminal-green containers/badges
val MatrixBorderGreen = Color(0x6622C55E) // green-500 @ 40% — standard border strength
val MatrixBorderGreenDim = Color(0x4D22C55E) // green-500 @ 30% — dimmer border (unselected tabs)
val MatrixGlow = Color(0xFF00FF66) // the spec's literal neon glow color, used only for shadow/glow effects

val MatrixAccentGradient = Brush.horizontalGradient(listOf(MatrixPrimaryGreen, MatrixBrightGreen))

private val MoviesSwatch = OrangeAccent
private val AnimalsSwatch = Color(0xFF22C55E)
private val MusicSwatch = Color(0xFFEC4899)
private val ActionsSwatch = Color(0xFF3B82F6)
private val FamousPeopleSwatch = Color(0xFFF59E0B)
private val RandomObjectsSwatch = Color(0xFF06B6D4)
private val FoodSwatch = Color(0xFFDC2626)

val Category.swatchColor: Color
    get() = when (this) {
        Category.MOVIES -> MoviesSwatch
        Category.ANIMALS -> AnimalsSwatch
        Category.MUSIC -> MusicSwatch
        Category.ACTIONS -> ActionsSwatch
        Category.FAMOUS_PEOPLE -> FamousPeopleSwatch
        Category.RANDOM_OBJECTS -> RandomObjectsSwatch
        Category.FOOD -> FoodSwatch
    }

// Preview swatches for the Settings screen's theme picker card — each theme's dominant
// background and accent, used for the little avatar/color-chip preview, not the live theme itself.
val AppTheme.previewBackground: Color
    get() = when (this) {
        AppTheme.DEFAULT -> SurfaceDark
        AppTheme.MATRIX_TERMINAL -> MatrixBackground
        AppTheme.POP_ART -> BackgroundPopArt
        AppTheme.CYBERPUNK_NEON -> CyberBackground
    }

val AppTheme.previewAccent: Color
    get() = when (this) {
        AppTheme.DEFAULT -> OrangeAccent
        AppTheme.MATRIX_TERMINAL -> MatrixPrimaryGreen
        AppTheme.POP_ART -> PopArtPink600
        AppTheme.CYBERPUNK_NEON -> CyberPink500
    }
