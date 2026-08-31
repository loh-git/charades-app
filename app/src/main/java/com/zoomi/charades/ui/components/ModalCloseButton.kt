package com.zoomi.charades.ui.components

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zoomi.charades.ui.theme.LocalExtendedColors

/**
 * The app's one modal-header close (X) button — Settings, Stats, Create Custom Deck. Uses a
 * manual press-flash instead of the default ripple for the same reason as [NeutralButton]: a
 * flat dark background makes the ripple's blurred edge read as grainy.
 */
@Composable
fun ModalCloseButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val extended = LocalExtendedColors.current
    var flashTrigger by remember { mutableIntStateOf(0) }
    val background = remember(extended.closeIconBackground) { Animatable(extended.closeIconBackground) }
    LaunchedEffect(flashTrigger, extended) {
        if (flashTrigger > 0) {
            background.snapTo(extended.closeIconBackgroundPressed)
            background.animateTo(extended.closeIconBackground, animationSpec = tween(250))
        }
    }
    val onClickWithHaptic = hapticClick(onClick)
    Box(
        modifier = modifier
            .background(background.value, RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    flashTrigger++
                    onClickWithHaptic()
                },
            )
            .padding(8.dp),
    ) {
        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = extended.closeIconTint)
    }
}
