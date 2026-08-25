package com.zoomi.charades.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

/**
 * Renders modal-style content (a centered card over a dim scrim) directly in the caller's own
 * window, as the in-window replacement for [androidx.compose.ui.window.Dialog]. A Compose
 * Dialog opens a brand new Android window, which starts with the system status/nav bars visible
 * and only gets MainActivity's hide-until-swiped setup reapplied a frame or two later — that gap
 * is what shows as a visible bar flash on every screen that used Dialog. Rendering the same
 * scrim+card look as a plain composable avoids creating a new window at all, so the bars simply
 * stay however MainActivity already left them.
 *
 * `content` runs inside a [BoxWithConstraintsScope] so callers can size their card off the
 * screen's *actual* available height/width (via [BoxWithConstraintsScope.maxHeight]/`maxWidth`,
 * or [responsiveModalHeight]) rather than a dp constant tuned for one reference device — see
 * [responsiveModalHeight] for why a fixed dp height doesn't hold up across screen sizes.
 */
@Composable
fun ModalScaffold(onDismissRequest: () -> Unit, content: @Composable BoxWithConstraintsScope.() -> Unit) {
    BackHandler(onBack = onDismissRequest)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismissRequest,
            ),
        contentAlignment = Alignment.Center,
    ) {
        BoxWithConstraints(
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {},
            ),
        ) {
            content()
        }
    }
}

/**
 * A dp value works identically across screen densities, but not across screen *sizes* — a fixed
 * `.height(740.dp)` tuned to look right on the OnePlus Nord 3 5G (~915dp tall) is 80% of that
 * device's screen, but the exact same 740dp on a ~650dp-tall compact phone would overflow the
 * viewport entirely, since [ModalScaffold] only centers the card and doesn't scroll around it.
 * Call this instead of hardcoding `.height(preferred)`: it keeps `preferred` on screens roomy
 * enough for it, and scales down proportionally (by [fraction] of the real available height) on
 * anything smaller.
 */
@Composable
fun BoxWithConstraintsScope.responsiveModalHeight(preferred: Dp, fraction: Float = 0.88f): Dp =
    minOf(preferred, maxHeight * fraction)
