package com.zoomi.charades.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zoomi.charades.ui.theme.NeutralButtonColors

/** The app's one modal-header close (X) button — Settings, Stats, Create Custom Deck. */
@Composable
fun ModalCloseButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(NeutralButtonColors.closeIconBackground, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(8.dp),
    ) {
        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = NeutralButtonColors.closeIconTint)
    }
}
