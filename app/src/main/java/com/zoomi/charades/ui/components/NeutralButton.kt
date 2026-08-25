package com.zoomi.charades.ui.components

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import com.zoomi.charades.ui.theme.NeutralButtonColors

/**
 * The app's one neutral/secondary button style — Cancel, Close, Exit, Back, All Decks, Add,
 * Shuffle, etc. See [NeutralButtonColors] for why this exists as a single shared component
 * instead of each screen styling its own "neutral" button.
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
    var flashTrigger by remember { mutableIntStateOf(0) }
    val background = remember { Animatable(NeutralButtonColors.actionBackground) }
    LaunchedEffect(flashTrigger) {
        if (flashTrigger > 0) {
            background.snapTo(NeutralButtonColors.actionBackgroundPressed)
            background.animateTo(NeutralButtonColors.actionBackground, animationSpec = tween(250))
        }
    }
    val onClickWithHaptic = hapticClick(onClick)
    Row(
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
            .padding(PaddingValues(horizontal = 24.dp, vertical = 10.dp)),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Icon(imageVector = icon, contentDescription = null, tint = NeutralButtonColors.actionText, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(text, color = NeutralButtonColors.actionText, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}
