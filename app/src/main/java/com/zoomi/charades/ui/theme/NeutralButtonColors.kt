package com.zoomi.charades.ui.theme

import androidx.compose.ui.graphics.Color

// Sunset Arcade's neutral/greyscale button palette — consolidated from what had drifted into
// ~9 near-duplicate hardcoded hex combinations spread across Settings, Stats, DeckDetail,
// CreateCustomDeck, RoundSummary, DeckList, and GameScreen. Every neutral/secondary button or
// pill in the app should now pull from one of these three token sets rather than inlining its
// own hex values, so a "Cancel" button and a "Back" button (for example) are guaranteed to look
// identical rather than accidentally close-but-not-quite.
object NeutralButtonColors {
    // Full-width/weighted neutral action buttons: Cancel, Close, Exit, Back, All Decks, Add,
    // Shuffle. Anything that reads as "a secondary button you can tap", as opposed to the
    // amber primary CTA or an unselected choice pill.
    val actionBackground = Color(0xFF182234)
    val actionBackgroundPressed = Color(0xFF2E3F58)
    val actionText = Color(0xFFCAD5E2)

    // Unselected state of any multi-choice pill row (round duration, tilt sensitivity, mode,
    // category filters). Shares actionBackground's hue family but stays visually distinct via
    // a dimmer text color and a visible border, since these sit shoulder-to-shoulder with an
    // amber-selected sibling and need a defined edge.
    val pillBackground = Color(0xFF182234)
    val pillBorder = Color(0xFF212D42)
    val pillText = Color(0xFF90A1B9)

    // Modal header close (X) icon buttons — filled with the modal's own surface colour so the
    // tap target is invisible until interacted with, matching Sunset Arcade's modal surface.
    val closeIconBackground = Color(0xFF0F172A)
    val closeIconBackgroundPressed = Color(0xFF243044)
    val closeIconTint = Color(0xFF94A3B8)
}
