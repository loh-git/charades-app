package com.zoomi.charades.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zoomi.charades.ui.theme.NeutralButtonColors

private val Amber500 = Color(0xFFF59E0B)
private val Slate950 = Color(0xFF020617)

/**
 * The app's one "unselected choice pill" style — round duration, tilt sensitivity, party mode,
 * etc. Selected state is always amber/slate-950; unselected pulls from [NeutralButtonColors].
 */
@Composable
fun OptionPill(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(12.dp)
    Box(
        modifier = modifier
            .background(if (selected) Amber500 else NeutralButtonColors.pillBackground, shape)
            .then(if (!selected) Modifier.border(1.dp, NeutralButtonColors.pillBorder, shape) else Modifier)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Black else FontWeight.Bold,
            color = if (selected) Slate950 else NeutralButtonColors.pillText,
        )
    }
}
