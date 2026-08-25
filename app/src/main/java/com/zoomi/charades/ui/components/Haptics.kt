package com.zoomi.charades.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import com.zoomi.charades.ui.theme.LocalHapticsEnabled

/**
 * Wraps [onClick] so every tap fires a short haptic tick first, honoring the user's "Haptic
 * Feedback" setting — the single place button components should route their click through
 * instead of each screen wiring up [LocalHapticFeedback] itself.
 */
@Composable
fun hapticClick(onClick: () -> Unit): () -> Unit {
    val haptics = LocalHapticFeedback.current
    val hapticsEnabled = LocalHapticsEnabled.current
    return {
        if (hapticsEnabled) haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        onClick()
    }
}
