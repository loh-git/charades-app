package com.zoomi.charades.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
 */
@Composable
fun NeutralButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = NeutralButtonColors.actionBackground,
            contentColor = NeutralButtonColors.actionText,
        ),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 10.dp),
    ) {
        if (icon != null) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(text, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}
