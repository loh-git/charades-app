package com.zoomi.charades.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zoomi.charades.ui.theme.LocalExtendedColors
import com.zoomi.charades.ui.theme.hardShadow
import com.zoomi.charades.ui.theme.themedGlow

@Composable
fun ScreenHeaderIcon(icon: String, modifier: Modifier = Modifier, size: Dp = 44.dp) {
    val extended = LocalExtendedColors.current
    val shape = RoundedCornerShape(14.dp)
    Box(
        modifier = modifier
            .size(size)
            .themedGlow(extended, shape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.18f), shape),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = icon, fontSize = (size.value * 0.5f).sp)
    }
}

@Composable
fun ScreenHeaderIcon(icon: ImageVector, contentDescription: String? = null, modifier: Modifier = Modifier, size: Dp = 44.dp) {
    val extended = LocalExtendedColors.current
    val shape = RoundedCornerShape(12.dp)
    Box(
        modifier = modifier
            .size(size)
            .themedGlow(extended, shape, color = extended.iconBadgeGlowColor, elevation = extended.iconBadgeGlowElevation)
            .hardShadow(extended, shape, large = false)
            .background(extended.headerIconBackground ?: MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), shape)
            .border(
                extended.iconBadgeBorderWidth,
                extended.headerIconBorder ?: MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                shape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = extended.headerIconTint ?: MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(size * 0.5f),
        )
    }
}
