package com.zoomi.charades.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.zoomi.charades.data.Category
import com.zoomi.charades.ui.components.CategoryPill
import com.zoomi.charades.ui.components.ModalCloseButton
import com.zoomi.charades.ui.theme.iconVector
import com.zoomi.charades.ui.viewmodel.CreateCustomDeckViewModel

private val ModalSurface = Color(0xFF0F172A) // slate-900, opaque
private val BorderSlate800 = Color(0xFF1E293B)
private val InputSurface = Color(0xCC1E293B) // slate-800/80
private val InputBorder = Color(0xFF334155) // slate-700
private val Amber300 = Color(0xFFFCD34D)
private val Amber500 = Color(0xFFF59E0B)
private val AmberChipBg = Color(0x33F59E0B) // amber-500/20
private val AmberChipBorder = Color(0x4DF59E0B) // amber-500/30
private val Slate950 = Color(0xFF020617)
private val Slate500 = Color(0xFF64748B)
private val TextSlate100 = Color(0xFFF8FAFC)
private val TextSlate400 = Color(0xFF94A3B8)
private val WhiteBorder10 = Color(0x1AFFFFFF)
private val BlackBg20 = Color(0x33000000)

@Composable
fun CreateCustomDeckScreen(
    viewModel: CreateCustomDeckViewModel,
    onClose: () -> Unit,
) {
    Dialog(onDismissRequest = onClose, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Column(
            modifier = Modifier
                .widthIn(max = 576.dp)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .background(ModalSurface, RoundedCornerShape(24.dp))
                .border(1.dp, BorderSlate800, RoundedCornerShape(24.dp))
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
        ) {
            HeaderRow(onClose = onClose)

            FieldLabel("Deck Title", modifier = Modifier.padding(top = 20.dp))
            StyledTextField(
                value = viewModel.title,
                onValueChange = viewModel::onTitleChange,
                placeholder = "e.g. Inside Jokes, Anime Heroes, 90s Nostalgia",
                modifier = Modifier.padding(top = 6.dp),
            )

            FieldLabel("Category", modifier = Modifier.padding(top = 16.dp))
            val customCategoryNames by viewModel.customCategoryNames.collectAsState()
            CategorySelector(
                selectedBuiltIn = viewModel.category,
                selectedCustom = viewModel.selectedCustomCategory,
                customCategoryNames = customCategoryNames,
                onSelectBuiltIn = viewModel::onCategoryChange,
                onSelectCustom = viewModel::onCustomCategorySelect,
                onAddCustom = viewModel::addCustomCategory,
                modifier = Modifier.padding(top = 6.dp),
            )

            FieldLabel("Description", modifier = Modifier.padding(top = 16.dp))
            StyledTextField(
                value = viewModel.description,
                onValueChange = viewModel::onDescriptionChange,
                placeholder = "Short deck description...",
                modifier = Modifier.padding(top = 6.dp),
            )

            FieldLabel("How to Play Instructions", modifier = Modifier.padding(top = 16.dp))
            StyledTextField(
                value = viewModel.howToPlay,
                onValueChange = viewModel::onHowToPlayChange,
                placeholder = "e.g., Hum the song, act out the character...",
                modifier = Modifier.padding(top = 6.dp),
            )

            FieldLabel(
                "Cards / Words List (${viewModel.words.size} added)",
                modifier = Modifier.padding(top = 16.dp),
            )
            WordListBuilder(
                wordInput = viewModel.wordInput,
                onWordInputChange = viewModel::onWordInputChange,
                onAddWord = viewModel::addWord,
                words = viewModel.words,
                onRemoveWord = viewModel::removeWord,
                modifier = Modifier.padding(top = 6.dp),
            )

            Box(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .fillMaxWidth()
                    .background(WhiteBorder10)
                    .height(1.dp),
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Button(
                    onClick = onClose,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BorderSlate800, contentColor = Color.White),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 10.dp),
                ) {
                    Text("Cancel", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = { viewModel.save(onSaved = onClose) },
                    enabled = viewModel.isValid,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Amber500,
                        contentColor = Slate950,
                        disabledContainerColor = Amber500.copy(alpha = 0.4f),
                        disabledContentColor = Slate950.copy(alpha = 0.6f),
                    ),
                ) {
                    Text("Save Deck", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                }
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
        Column {
            Text(text = "Create Custom Deck", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextSlate100)
            Text(text = "Build your personalised charades deck", fontSize = 12.sp, color = TextSlate400)
        }
        ModalCloseButton(onClick = onClose)
    }
}

@Composable
private fun FieldLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = TextSlate400,
        modifier = modifier,
    )
}

@Composable
private fun StyledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    onSubmit: (() -> Unit)? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = Slate500, fontSize = 14.sp) },
        singleLine = true,
        textStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = InputSurface,
            unfocusedContainerColor = InputSurface,
            focusedBorderColor = Amber500,
            unfocusedBorderColor = InputBorder,
            cursorColor = Amber500,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
        ),
        keyboardOptions = if (onSubmit != null) KeyboardOptions(imeAction = ImeAction.Done) else KeyboardOptions.Default,
        keyboardActions = KeyboardActions(onDone = { onSubmit?.invoke() }),
        modifier = modifier.fillMaxWidth(),
    )
}

@Composable
private fun CategorySelector(
    selectedBuiltIn: Category,
    selectedCustom: String?,
    customCategoryNames: List<String>,
    onSelectBuiltIn: (Category) -> Unit,
    onSelectCustom: (String) -> Unit,
    onAddCustom: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showNewCategoryInput by remember { mutableStateOf(false) }
    var newCategoryText by remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Category.entries.forEach { category ->
                CategoryPill(
                    icon = category.iconVector,
                    label = category.displayName,
                    selected = selectedCustom == null && selectedBuiltIn == category,
                    onClick = { onSelectBuiltIn(category) },
                )
            }
            customCategoryNames.forEach { name ->
                CategoryPill(
                    icon = null,
                    label = name,
                    selected = selectedCustom == name,
                    onClick = { onSelectCustom(name) },
                )
            }
            CategoryPill(
                icon = null,
                label = "+ New",
                selected = false,
                onClick = { showNewCategoryInput = !showNewCategoryInput },
            )
        }

        if (showNewCategoryInput) {
            val submitNewCategory = {
                if (newCategoryText.isNotBlank()) {
                    onAddCustom(newCategoryText)
                    newCategoryText = ""
                    showNewCategoryInput = false
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                StyledTextField(
                    value = newCategoryText,
                    onValueChange = { newCategoryText = it },
                    placeholder = "New category name...",
                    modifier = Modifier.weight(1f),
                    onSubmit = submitNewCategory,
                )
                Button(
                    onClick = submitNewCategory,
                    modifier = Modifier.height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BorderSlate800, contentColor = Color.White),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 10.dp),
                ) {
                    Text("Add", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun WordListBuilder(
    wordInput: String,
    onWordInputChange: (String) -> Unit,
    onAddWord: () -> Unit,
    words: List<String>,
    onRemoveWord: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            StyledTextField(
                value = wordInput,
                onValueChange = onWordInputChange,
                placeholder = "Type a word...",
                modifier = Modifier.weight(1f),
                onSubmit = onAddWord,
            )
            Button(
                onClick = onAddWord,
                modifier = Modifier.height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BorderSlate800, contentColor = Color.White),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 10.dp),
            ) {
                Text("Add", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }

        Box(
            modifier = Modifier
                .padding(top = 12.dp)
                .fillMaxWidth()
                .heightIn(max = 144.dp)
                .background(BlackBg20, RoundedCornerShape(12.dp))
                .border(1.dp, WhiteBorder10, RoundedCornerShape(12.dp))
                .verticalScroll(rememberScrollState())
                .padding(8.dp),
        ) {
            if (words.isEmpty()) {
                Text(
                    text = "No words added yet. Add at least 3 words to create deck.",
                    fontSize = 12.sp,
                    fontStyle = FontStyle.Italic,
                    color = TextSlate400,
                    modifier = Modifier.padding(8.dp),
                )
            } else {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    words.forEachIndexed { index, word ->
                        WordChip(word = word, onRemove = { onRemoveWord(index) })
                    }
                }
            }
        }
    }
}

@Composable
private fun WordChip(word: String, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .background(AmberChipBg, RoundedCornerShape(8.dp))
            .border(1.dp, AmberChipBorder, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(text = word, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Amber300)
        Text(
            text = "×",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextSlate400,
            modifier = Modifier.clickable(onClick = onRemove),
        )
    }
}
