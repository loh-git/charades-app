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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zoomi.charades.ui.theme.LocalExtendedColors
import com.zoomi.charades.ui.theme.hardShadow
import com.zoomi.charades.ui.theme.themedGlow

/**
 * The app's one "unselected choice pill" style — round duration, tilt sensitivity, party mode,
 * etc. Selected state uses the active theme's primary/onPrimary; unselected pulls from
 * [com.zoomi.charades.ui.theme.ExtendedColors]'s neutral pill tokens.
 */
@Composable
fun OptionPill(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    // Overrides for OptionPill's two special-cased roles (round-duration timer pills, Deck
    // Detail's Classic/Party mode pills) — both no-ops unless a theme sets the matching
    // ExtendedColors field, so every other call site (and every other theme) is unaffected.
    selectedGradient: Brush? = null,
    selectedGlowColor: Color = Color.Transparent,
    selectedGlowElevation: Dp = 0.dp,
) {
    val extended = LocalExtendedColors.current
    val shape = RoundedCornerShape(12.dp)
    val borderWidth = if (selected) extended.selectedPillBorderWidth else extended.pillBorderWidth
    val borderColor = if (selected) extended.selectedPillBorderColor else extended.pillBorder
    Box(
        modifier = modifier
            .then(if (selected) Modifier.hardShadow(extended, shape, large = false) else Modifier)
            .then(
                if (selected && selectedGlowColor != Color.Transparent) {
                    Modifier.themedGlow(extended, shape, color = selectedGlowColor, elevation = selectedGlowElevation)
                } else {
                    Modifier
                },
            )
            .background(if (selected) MaterialTheme.colorScheme.primary else extended.pillBackground, shape)
            .then(if (selected && selectedGradient != null) Modifier.background(selectedGradient, shape) else Modifier)
            .then(if (borderWidth > 0.dp) Modifier.border(borderWidth, borderColor, shape) else Modifier)
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
