package com.zoomi.charades.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zoomi.charades.ui.theme.LocalExtendedColors

/**
 * The app's one "unselected choice pill" style — round duration, tilt sensitivity, party mode,
 * etc. Selected state uses the active theme's primary/onPrimary; unselected pulls from
 * [com.zoomi.charades.ui.theme.ExtendedColors]'s neutral pill tokens.
 */
@Composable
fun OptionPill(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val extended = LocalExtendedColors.current
    val shape = RoundedCornerShape(12.dp)
    Box(
        modifier = modifier
            .background(if (selected) MaterialTheme.colorScheme.primary else extended.pillBackground, shape)
            .then(if (!selected) Modifier.border(1.dp, extended.pillBorder, shape) else Modifier)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = hapticClick(onClick),
            )
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.Black else FontWeight.Bold,
            color = if (selected) MaterialTheme.colorScheme.onPrimary else extended.pillText,
        )
    }
}
