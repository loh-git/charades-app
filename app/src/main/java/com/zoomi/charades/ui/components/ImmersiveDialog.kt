package com.zoomi.charades.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

/**
 * Every Compose [androidx.compose.ui.window.Dialog] opens its own Android Window, which doesn't
 * inherit MainActivity's immersive (system-bars-hidden) setup — without this, the status/nav
 * bars reappear the moment any dialog screen opens. Call this once at the top of a Dialog's
 * content lambda to match MainActivity's hide-until-swiped behavior in that dialog's window too.
 */
@Composable
fun ImmersiveDialog() {
    val view = LocalView.current
    SideEffect {
        val window = (view.parent as? DialogWindowProvider)?.window ?: return@SideEffect
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}
