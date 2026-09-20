package com.zoomi.charades.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.zoomi.charades.ui.theme.LocalExtendedColors
import com.zoomi.charades.ui.theme.hardShadow
import com.zoomi.charades.ui.theme.themedGlow

@Composable
fun CategoryPill(
    icon: ImageVector?,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconAtEnd: Boolean = false,
) {
    val extended = LocalExtendedColors.current
    val shape = RoundedCornerShape(extended.cardCornerRadius)
    val background = if (selected) extended.selectedPillBackground ?: MaterialTheme.colorScheme.primary else extended.pillBackground
    val contentColor = if (selected) extended.selectedPillContent ?: MaterialTheme.colorScheme.onPrimary else extended.pillText
    val borderWidth = if (selected) extended.selectedPillBorderWidth else extended.pillBorderWidth
    val borderColor = if (selected) extended.selectedPillBorderColor else extended.pillBorder
    Row(
        modifier = modifier
            .then(if (selected) Modifier.themedGlow(extended, shape).hardShadow(extended, shape, large = false) else Modifier)
            .background(background, shape)
            .then(if (borderWidth > 0.dp) Modifier.border(borderWidth, borderColor, shape) else Modifier)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = hapticClick(onClick),
            )
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        if (icon != null && !iconAtEnd) {
            Icon(imageVector = icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(14.dp))
        }
        Text(text = label, color = contentColor, style = MaterialTheme.typography.labelLarge)
        if (icon != null && iconAtEnd) {
            Icon(imageVector = icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(14.dp))
        }
    }
}
