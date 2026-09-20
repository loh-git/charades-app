package com.zoomi.charades.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
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
import com.zoomi.charades.data.Category
import com.zoomi.charades.ui.components.CategoryPill
import com.zoomi.charades.ui.components.ModalCloseButton
import com.zoomi.charades.ui.components.ModalScaffold
import com.zoomi.charades.ui.components.responsiveModalHeight
import com.zoomi.charades.ui.components.NeutralButton
import com.zoomi.charades.ui.components.hapticClick
import com.zoomi.charades.ui.theme.LocalExtendedColors
import com.zoomi.charades.ui.theme.hardShadow
import com.zoomi.charades.ui.theme.iconVector
import com.zoomi.charades.ui.theme.primaryButtonColors
import com.zoomi.charades.ui.theme.primaryButtonGradientBackground
import com.zoomi.charades.ui.theme.themedGlow
import com.zoomi.charades.ui.viewmodel.CreateCustomDeckViewModel

private val ModalSurface: Color @Composable get() = LocalExtendedColors.current.modalSurface
private val BorderSlate800: Color @Composable get() = LocalExtendedColors.current.modalBorder
private val InputSurface: Color @Composable get() = LocalExtendedColors.current.inputSurface
private val InputBorder: Color @Composable get() = MaterialTheme.colorScheme.outline
private val Amber300: Color @Composable get() = LocalExtendedColors.current.accentBright
private val Amber500: Color @Composable get() = MaterialTheme.colorScheme.primary
private val AmberChipBg: Color @Composable get() = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
private val AmberChipBorder: Color @Composable get() = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
private val Slate500: Color @Composable get() = LocalExtendedColors.current.inputPlaceholder
private val TextSlate100: Color @Composable get() = LocalExtendedColors.current.textStrong
private val TextSlate400: Color @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant
private val WhiteBorder10: Color @Composable get() = LocalExtendedColors.current.dividerFaint
private val BlackBg20: Color @Composable get() = LocalExtendedColors.current.recessedContainerBackground

@Composable
fun CreateCustomDeckScreen(
    viewModel: CreateCustomDeckViewModel,
    onClose: () -> Unit,
) {
    val createDeckModalShape = RoundedCornerShape(24.dp)
    ModalScaffold(onDismissRequest = onClose) {
        Column(
            modifier = Modifier
                .widthIn(max = 560.dp)
                .fillMaxWidth()
                .height(responsiveModalHeight(740.dp))
                .padding(horizontal = 16.dp)
                .hardShadow(LocalExtendedColors.current, createDeckModalShape, large = true)
                .themedGlow(LocalExtendedColors.current, createDeckModalShape, color = LocalExtendedColors.current.cardGlowColor, elevation = LocalExtendedColors.current.cardGlowElevation)
                .background(ModalSurface, createDeckModalShape)
                .border(LocalExtendedColors.current.modalBorderWidth, BorderSlate800, createDeckModalShape)
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
                NeutralButton(
                    text = "Cancel",
                    onClick = onClose,
                    modifier = Modifier.weight(1f).height(48.dp),
                )
                val extended = LocalExtendedColors.current
                val saveDeckShape = RoundedCornerShape(12.dp)
                // Same button chrome as the Add (word/category) buttons below — hardShadow,
                // themedGlow, primaryButtonGradientBackground and primaryButtonColors applied
                // unconditionally, no bespoke disabled-state colors. primaryButtonColors keeps
                // its disabled colors identical to the enabled ones (see ExtendedColors.kt), so
                // this always renders exactly like Add regardless of viewModel.isValid.
                Button(
                    onClick = hapticClick { viewModel.save(onSaved = onClose) },
                    enabled = viewModel.isValid,
                    modifier = Modifier.weight(1f)
                        .hardShadow(extended, saveDeckShape, large = true)
                        .themedGlow(extended, saveDeckShape, color = extended.primaryButtonGlowColor, elevation = extended.primaryButtonGlowElevation)
                        .primaryButtonGradientBackground(extended, saveDeckShape),
                    shape = saveDeckShape,
                    colors = primaryButtonColors(extended),
                    border = if (extended.primaryButtonBorderWidth > 0.dp) BorderStroke(extended.primaryButtonBorderWidth, extended.primaryButtonBorderColor) else null,
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp),
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
        textStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = InputSurface,
            unfocusedContainerColor = InputSurface,
            focusedBorderColor = Amber500,
            unfocusedBorderColor = InputBorder,
            cursorColor = Amber500,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
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
                icon = Icons.Filled.Add,
                label = "New",
                selected = false,
                onClick = { showNewCategoryInput = !showNewCategoryInput },
                iconAtEnd = true,
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
                val addCategoryExtended = LocalExtendedColors.current
                val addCategoryShape = RoundedCornerShape(12.dp)
                Button(
                    onClick = hapticClick(submitNewCategory),
                    modifier = Modifier.height(56.dp)
                        .hardShadow(addCategoryExtended, addCategoryShape, large = true)
                        .themedGlow(addCategoryExtended, addCategoryShape, color = addCategoryExtended.primaryButtonGlowColor, elevation = addCategoryExtended.primaryButtonGlowElevation)
                        .primaryButtonGradientBackground(addCategoryExtended, addCategoryShape),
                    shape = addCategoryShape,
                    colors = primaryButtonColors(addCategoryExtended),
                    border = if (addCategoryExtended.primaryButtonBorderWidth > 0.dp) BorderStroke(addCategoryExtended.primaryButtonBorderWidth, addCategoryExtended.primaryButtonBorderColor) else null,
                ) {
                    Text("Add", fontWeight = FontWeight.Bold)
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
            val addWordExtended = LocalExtendedColors.current
            val addWordShape = RoundedCornerShape(12.dp)
            Button(
                onClick = hapticClick(onAddWord),
                modifier = Modifier.height(56.dp)
                    .hardShadow(addWordExtended, addWordShape, large = true)
                    .themedGlow(addWordExtended, addWordShape, color = addWordExtended.primaryButtonGlowColor, elevation = addWordExtended.primaryButtonGlowElevation)
                    .primaryButtonGradientBackground(addWordExtended, addWordShape),
                shape = addWordShape,
                colors = primaryButtonColors(addWordExtended),
                border = if (addWordExtended.primaryButtonBorderWidth > 0.dp) BorderStroke(addWordExtended.primaryButtonBorderWidth, addWordExtended.primaryButtonBorderColor) else null,
            ) {
                Text("Add")
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
            modifier = Modifier.clickable(onClick = hapticClick(onRemove)),
        )
    }
}
