package com.zoomi.charades.ui.theme

import androidx.compose.runtime.compositionLocalOf

/**
 * Whether the user's "Haptic Feedback" setting is on — provided once from MainActivity so any
 * button anywhere in the tree can fire a tap haptic without needing GameSettings threaded down
 * through every screen's parameter list.
 */
val LocalHapticsEnabled = compositionLocalOf { true }
