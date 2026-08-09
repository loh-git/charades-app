package com.zoomi.charades.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zoomi.charades.data.Category
import com.zoomi.charades.ui.viewmodel.CreateCustomDeckViewModel

@Composable
fun CreateCustomDeckScreen(
    viewModel: CreateCustomDeckViewModel,
    onClose: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
    ) {
        HeaderRow(onClose = onClose)

        Text("Deck Title *", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(top = 20.dp))
        OutlinedTextField(
            value = viewModel.title,
            onValueChange = viewModel::onTitleChange,
            placeholder = { Text("e.g., 80s Anime, Office Jokes, Family Memories") },
            modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
            singleLine = true,
        )

        Text("Category", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(top = 16.dp))
        CategoryDropdown(
            selected = viewModel.category,
            onSelect = viewModel::onCategoryChange,
            modifier = Modifier.padding(top = 6.dp),
        )

        Text("Theme Description", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(top = 16.dp))
        OutlinedTextField(
            value = viewModel.description,
            onValueChange = viewModel::onDescriptionChange,
            placeholder = { Text("Short description of what this deck is about") },
            modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
        )

        Text("How to Play Instructions", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(top = 16.dp))
        OutlinedTextField(
            value = viewModel.howToPlay,
            onValueChange = viewModel::onHowToPlayChange,
            placeholder = { Text("e.g., Hum the song, act out the character...") },
            modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
        )

        Text(
            "Word Cards List * (Separate with commas or newlines)",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(top = 16.dp),
        )
        OutlinedTextField(
            value = viewModel.wordsText,
            onValueChange = viewModel::onWordsChange,
            placeholder = { Text("Pikachu, Mario, Zelda, Master Chief, Sonic...") },
            modifier = Modifier.fillMaxWidth().height(120.dp).padding(top = 6.dp),
        )

        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Button(
                onClick = { viewModel.save(onSaved = onClose) },
                enabled = viewModel.isValid,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Create Deck")
            }
            OutlinedButton(onClick = onClose, modifier = Modifier.fillMaxWidth()) {
                Text("Cancel")
            }
        }
    }
}

@Composable
private fun HeaderRow(onClose: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "✨ Create Custom Deck",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.clickable(onClick = onClose).padding(4.dp),
        )
    }
}

@Composable
private fun CategoryDropdown(
    selected: Category,
    onSelect: (Category) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier.fillMaxWidth()) {
        OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
            Text("${selected.icon} ${selected.displayName}")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            Category.entries.forEach { category ->
                DropdownMenuItem(
                    text = { Text("${category.icon} ${category.displayName}") },
                    onClick = {
                        onSelect(category)
                        expanded = false
                    },
                )
            }
        }
    }
}
