package com.zoomi.charades.ui.components

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zoomi.charades.ui.theme.LocalExtendedColors

/**
 * The app's one neutral/secondary button style — Cancel, Close, Exit, Back, All Decks, Add,
 * Shuffle, etc. Colors come from [com.zoomi.charades.ui.theme.ExtendedColors]'s neutral-button
 * tokens, so this exists as a single shared component instead of each screen styling its own
 * "neutral" button.
 *
 * Built on a plain clickable Row rather than Material3's Button: this Compose version's Button
 * always draws its own ripple() internally regardless of LocalIndication, and that ripple's
 * blurred fade edge reads as grainy over this button's flat dark background. Tapping instead
 * flashes the background to a lighter shade and quickly fades it back — a deliberate, non-ripple
 * press effect instead of fighting Button's hardcoded one.
 */
@Composable
fun NeutralButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
) {
    val extended = LocalExtendedColors.current
    var flashTrigger by remember { mutableIntStateOf(0) }
    val background = remember(extended.neutralButtonBackground) { Animatable(extended.neutralButtonBackground) }
    LaunchedEffect(flashTrigger, extended) {
        if (flashTrigger > 0) {
            background.snapTo(extended.neutralButtonBackgroundPressed)
            background.animateTo(extended.neutralButtonBackground, animationSpec = tween(250))
        }
    }
    val onClickWithHaptic = hapticClick(onClick)
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier = modifier
            .background(background.value, shape)
            .then(
                if (extended.neutralButtonBorderWidth > 0.dp) {
                    Modifier.border(extended.neutralButtonBorderWidth, extended.neutralButtonBorderColor, shape)
                } else {
                    Modifier
                },
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    flashTrigger++
                    onClickWithHaptic()
                },
            )
            .padding(PaddingValues(horizontal = 24.dp, vertical = 10.dp)),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Icon(imageVector = icon, contentDescription = null, tint = extended.neutralButtonText, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(text, color = extended.neutralButtonText, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}
