package com.zoomi.charades.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.zoomi.charades.ui.theme.LocalExtendedColors
import com.zoomi.charades.ui.theme.NeutralButtonColors

@Composable
fun CategoryPill(icon: ImageVector?, label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val extended = LocalExtendedColors.current
    val shape = RoundedCornerShape(extended.cardCornerRadius)
    val contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else NeutralButtonColors.pillText
    Row(
        modifier = modifier
            .background(
                if (selected) MaterialTheme.colorScheme.primary else NeutralButtonColors.pillBackground,
                shape,
            )
            .then(if (!selected) Modifier.border(1.dp, NeutralButtonColors.pillBorder, shape) else Modifier)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        if (icon != null) {
            Icon(imageVector = icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(14.dp))
        }
        Text(text = label, color = contentColor, style = MaterialTheme.typography.labelLarge)
    }
}
