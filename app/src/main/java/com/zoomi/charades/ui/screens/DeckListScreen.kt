package com.zoomi.charades.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zoomi.charades.data.Category
import com.zoomi.charades.data.CustomCategoryRepository
import com.zoomi.charades.data.Deck
import com.zoomi.charades.data.DeckRepository
import com.zoomi.charades.data.GameSettings
import com.zoomi.charades.data.SettingsRepository
import com.zoomi.charades.data.StatsRepository
import com.zoomi.charades.ui.components.CategoryPill
import com.zoomi.charades.ui.components.ModalScaffold
import com.zoomi.charades.ui.components.responsiveModalHeight
import com.zoomi.charades.ui.components.hapticClick
import com.zoomi.charades.ui.theme.LocalExtendedColors
import com.zoomi.charades.ui.theme.iconVector
import com.zoomi.charades.ui.theme.swatchColor
import com.zoomi.charades.ui.viewmodel.CreateCustomDeckViewModel
import com.zoomi.charades.ui.viewmodel.DeckFilter
import com.zoomi.charades.ui.viewmodel.DeckListViewModel
import com.zoomi.charades.ui.viewmodel.SettingsViewModel
import com.zoomi.charades.ui.viewmodel.StatsViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun DeckListScreen(
    viewModel: DeckListViewModel,
    deckRepository: DeckRepository,
    customCategoryRepository: CustomCategoryRepository,
    settingsRepository: SettingsRepository,
    statsRepository: StatsRepository,
    onStartGame: (deck: Deck, timerSeconds: Int) -> Unit,
    onStartPartyGame: (deck: Deck, timerSeconds: Int) -> Unit,
) {
    val extended = LocalExtendedColors.current
    val decks by viewModel.filteredDecks.collectAsState()
    val allDecks by viewModel.allDecks.collectAsState()
    val query by viewModel.searchQuery.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val favoriteDeckIds by viewModel.favoriteDeckIds.collectAsState()
    var shuffleDialogOpen by remember { mutableStateOf(false) }
    var settingsOpen by remember { mutableStateOf(false) }
    var statsOpen by remember { mutableStateOf(false) }
    var createCustomDeckOpen by remember { mutableStateOf(false) }
    var deckForDetail by remember { mutableStateOf<Deck?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            fullWidthItem {
                TopBar(
                    onShuffle = { shuffleDialogOpen = true },
                    onOpenStats = { statsOpen = true },
                    onOpenSettings = { settingsOpen = true },
                )
            }
            fullWidthItem {
                CategoryFilterRow(selected = selectedFilter, onSelect = viewModel::onFilterSelected)
            }
            fullWidthItem { SearchField(query = query, onQueryChange = viewModel::onSearchQueryChange) }
            fullWidthItem { AddCustomDeckButton { createCustomDeckOpen = true } }

            if (decks.isEmpty()) {
                fullWidthItem {
                    Text(
                        text = "No decks match your search.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 24.dp),
                    )
                }
            } else {
                items(decks, key = { it.id }) { deck ->
                    DeckCard(
                        deck = deck,
                        isFavorited = deck.id in favoriteDeckIds,
                        onClick = { deckForDetail = deck },
                        onToggleFavorite = { viewModel.onToggleFavorite(deck.id) },
                    )
                }
            }
        }

        if (extended.useBottomNav) {
            BottomNavBar(onOpenStats = { statsOpen = true }, onOpenSettings = { settingsOpen = true })
        }
    }

    if (shuffleDialogOpen && allDecks.isNotEmpty()) {
        ShuffleDeckDialog(
            decks = allDecks,
            onDismiss = { shuffleDialogOpen = false },
            onPlayDeck = { deck ->
                shuffleDialogOpen = false
                deckForDetail = deck
            },
        )
    }

    if (settingsOpen) {
        val settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory(settingsRepository))
        SettingsScreen(viewModel = settingsViewModel, onClose = { settingsOpen = false })
    }

    if (statsOpen) {
        val statsViewModel: StatsViewModel = viewModel(factory = StatsViewModel.Factory(statsRepository))
        StatsScreen(viewModel = statsViewModel, onClose = { statsOpen = false })
    }

    if (createCustomDeckOpen) {
        val createCustomDeckViewModel: CreateCustomDeckViewModel =
            viewModel(factory = CreateCustomDeckViewModel.Factory(deckRepository, customCategoryRepository))
        CreateCustomDeckScreen(viewModel = createCustomDeckViewModel, onClose = { createCustomDeckOpen = false })
    }

    deckForDetail?.let { deck ->
        val settings by settingsRepository.settings.collectAsState(initial = GameSettings())
        DeckDetailScreen(
            deck = deck,
            defaultTimerSeconds = settings.defaultRoundDurationSeconds,
            onStart = { timerSeconds, isPartyMode ->
                deckForDetail = null
                if (isPartyMode) onStartPartyGame(deck, timerSeconds) else onStartGame(deck, timerSeconds)
            },
            onBack = { deckForDetail = null },
        )
    }
}

private fun LazyGridScope.fullWidthItem(content: @Composable () -> Unit) {
    item(span = { GridItemSpan(maxLineSpan) }) { content() }
}

@Composable
private fun TopBar(onShuffle: () -> Unit, onOpenStats: () -> Unit, onOpenSettings: () -> Unit) {
    val extended = LocalExtendedColors.current
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
        horizontalArrangement = if (extended.showHeaderTitle) Arrangement.SpaceBetween else Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (extended.showHeaderTitle) {
            Text(
                text = buildAnnotatedString {
                    append("Ultimate ")
                    withStyle(SpanStyle(color = Color(0xFFFFB900))) { append("Charades") }
                },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                lineHeight = 28.sp,
                maxLines = 2,
                modifier = Modifier.weight(1f, fill = false).padding(end = 8.dp),
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TopBarIconButton(
                icon = Icons.Filled.Shuffle,
                contentDescription = "Shuffle a deck",
                onClick = onShuffle,
                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                contentColor = Color(0xFFD29704),
                spinOnClick = extended.playfulAnimations,
            )
            if (!extended.useBottomNav) {
                TopBarIconButton(
                    icon = Icons.Filled.BarChart,
                    contentDescription = "Stats",
                    onClick = onOpenStats,
                    containerColor = Color(0xCC1D293D),
                    borderColor = Color(0xFF334155),
                    contentColor = Color(0xFFE2E8F0),
                    flipHorizontally = true,
                )
                TopBarIconButton(
                    icon = Icons.Filled.Settings,
                    contentDescription = "Settings",
                    onClick = onOpenSettings,
                    containerColor = Color(0xFFFE9A00),
                    borderColor = null,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
//                    shape = RoundedCornerShape(extended.cardCornerRadius),
                )
            }
        }
    }
}

@Composable
private fun TopBarIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    containerColor: Color,
    borderColor: Color?,
    contentColor: Color,
    spinOnClick: Boolean = false,
    flipHorizontally: Boolean = false,
    shape: RoundedCornerShape = RoundedCornerShape(12.dp),
) {
    var spinTrigger by remember { mutableIntStateOf(0) }
    val rotation = remember { Animatable(0f) }
    LaunchedEffect(spinTrigger) {
        if (spinTrigger > 0) {
            rotation.animateTo(rotation.value + 360f, animationSpec = tween(500))
        }
    }
    Box(
        modifier = Modifier
            .size(44.dp)
            .background(containerColor, shape)
            .then(
                if (borderColor != null) {
                    Modifier.border(1.dp, borderColor, shape)
                } else {
                    Modifier
                },
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = hapticClick {
                    if (spinOnClick) spinTrigger++
                    onClick()
                },
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = contentColor,
            modifier = Modifier.graphicsLayer {
                if (spinOnClick) rotationZ = rotation.value
                if (flipHorizontally) scaleX = -1f
            },
        )
    }
}

@Composable
private fun BottomNavBar(onOpenStats: () -> Unit, onOpenSettings: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        BottomNavItem(icon = Icons.Filled.Folder, label = "Decks", selected = true, onClick = {})
        BottomNavItem(icon = Icons.Filled.BarChart, label = "Stats", selected = false, onClick = onOpenStats)
        BottomNavItem(icon = Icons.Filled.Settings, label = "Settings", selected = false, onClick = onOpenSettings)
    }
}

@Composable
private fun BottomNavItem(icon: ImageVector, label: String, selected: Boolean, onClick: () -> Unit) {
    val tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = hapticClick(onClick)).padding(horizontal = 16.dp, vertical = 4.dp),
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = tint,
            modifier = Modifier.padding(top = 2.dp),
        )
    }
}

@Composable
private fun CategoryFilterRow(selected: DeckFilter, onSelect: (DeckFilter) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),

        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        CategoryPill(
            icon = Icons.Filled.Star,
            label = "Favourites",
            selected = selected == DeckFilter.Favourites,
            onClick = { onSelect(DeckFilter.Favourites) },
        )
        CategoryPill(
            icon = Icons.Filled.Whatshot,
            label = "All Decks",
            selected = selected == DeckFilter.All,
            onClick = { onSelect(DeckFilter.All) },
        )
        CategoryPill(
            icon = Icons.Filled.Category,
            label = "Custom",
            selected = selected == DeckFilter.Custom,
            onClick = { onSelect(DeckFilter.Custom) },
        )
        Category.entries.forEach { category ->
            CategoryPill(
                icon = category.iconVector,
                label = category.displayName,
                selected = selected == DeckFilter.ByCategory(category),
                onClick = { onSelect(DeckFilter.ByCategory(category)) },
            )
        }
    }
}

@Composable
private fun SearchField(query: String, onQueryChange: (String) -> Unit) {
    val extended = LocalExtendedColors.current
    val placeholderColor = MaterialTheme.colorScheme.onSurfaceVariant
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp),
        placeholder = { Text("Search decks...", color = placeholderColor) },
        leadingIcon = { Icon(imageVector = Icons.Filled.Search, contentDescription = null, tint = placeholderColor) },
        singleLine = true,
        shape = RoundedCornerShape(extended.cardCornerRadius),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedLeadingIconColor = placeholderColor,
            focusedLeadingIconColor = placeholderColor,
        ),
    )
}

@Composable
private fun AddCustomDeckButton(onClick: () -> Unit) {
    val extended = LocalExtendedColors.current
    Button(
        onClick = hapticClick(onClick),
        modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
        shape = RoundedCornerShape(extended.cardCornerRadius),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = Color.Black),
    ) {
        Text(
            text = "+  Create Custom Deck",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp),
        )
    }
}

@Composable
private fun DeckCard(deck: Deck, isFavorited: Boolean, onClick: () -> Unit, onToggleFavorite: () -> Unit) {
    val extended = LocalExtendedColors.current
    val shape = RoundedCornerShape(extended.cardCornerRadius)
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (extended.playfulAnimations && isPressed) 0.95f else 1f,
        label = "deckCardScale",
    )
    val cardBackground = if (extended.useCategoryTintedCardBackground) {
        deck.category.swatchColor.copy(alpha = 0.22f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    // A Box (not a Column) so the favorite star can be an overlaid sibling with its own
    // clickable region, rather than a plain child of the card's own clickable Column — that
    // way a tap on the star is consumed by the star alone and never bubbles up to onClick, and
    // a tap anywhere else on the card never triggers onToggleFavorite.
    Box(
        modifier = Modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .fillMaxWidth()
            .then(if (extended.cardElevation > 0.dp) Modifier.shadow(extended.cardElevation, shape) else Modifier)
            .background(cardBackground, shape)
            .then(
                if (extended.cardBorderWidth > 0.dp) {
                    Modifier.border(extended.cardBorderWidth, extended.cardBorderColor, shape)
                } else {
                    Modifier
                },
            )
            .clickable(interactionSource = interactionSource, indication = null, onClick = hapticClick(onClick)),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .size(extended.deckIconSize)
                        .background(Color(0xFF3E2F20), RoundedCornerShape(16.dp))
                        .border(1.dp, Color(0xFF6C4818), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = if (deck.customCategoryName != null) Icons.Filled.Category else deck.category.iconVector,
                        contentDescription = deck.customCategoryName ?: deck.category.displayName,
                        tint = Color(0xFFFFB900),
                        modifier = Modifier.size(extended.deckIconSize * 0.5f),
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF3E2F20), RoundedCornerShape(50))
                                .border(1.dp, Color(0xFF6C4818), RoundedCornerShape(50))
                                .padding(horizontal = 8.dp, vertical = 3.dp),
                        ) {
                            Text(
                                text = deck.category.displayName,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFFFB900),
                            )
                        }
                        FavoriteStar(isFavorited = isFavorited, onToggleFavorite = onToggleFavorite)
                    }
                    Text(
                        text = "${deck.words.size} cards",
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }
            Text(
                text = deck.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            )
            Text(
                text = deck.shortDescription,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            )
            HorizontalDivider(modifier = Modifier.padding(top = 12.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 10.dp),
            ) {
                Text(
                    text = "Select Deck ",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(14.dp),
                )
            }
        }
    }
}

// A plain ripple on a small tap target like this renders as a visible rectangular bounding box
// (Compose's default indication isn't shape-aware without an explicit clip), which read as an
// unwanted opaque white square. Replaced with a custom circular blurred glow, colored to match
// the star's current state, that fades in and out on every tap instead.
@Composable
private fun FavoriteStar(isFavorited: Boolean, onToggleFavorite: () -> Unit) {
    val starColor = if (isFavorited) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
    var glowTrigger by remember { mutableIntStateOf(0) }
    val glowAlpha = remember { Animatable(0f) }
    LaunchedEffect(glowTrigger) {
        if (glowTrigger > 0) {
            glowAlpha.snapTo(0.9f)
            glowAlpha.animateTo(0f, animationSpec = tween(500))
        }
    }
    Box(contentAlignment = Alignment.Center) {
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = null,
            tint = starColor,
            modifier = Modifier
                .size(38.dp)
                .graphicsLayer { alpha = glowAlpha.value }
                .blur(6.dp),
        )
        Icon(
            imageVector = if (isFavorited) Icons.Filled.Star else Icons.Filled.StarBorder,
            contentDescription = if (isFavorited) "Remove from favorites" else "Add to favorites",
            tint = starColor,
            modifier = Modifier
                .size(26.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = hapticClick {
                        glowTrigger++
                        onToggleFavorite()
                    },
                )
                .padding(4.dp),
        )
    }
}

private fun List<Deck>.randomExcluding(exclude: Deck): Deck {
    val candidates = if (size > 1) filter { it.id != exclude.id } else this
    return candidates.random()
}

@Composable
private fun ShuffleDeckDialog(decks: List<Deck>, onDismiss: () -> Unit, onPlayDeck: (Deck) -> Unit) {
    val extended = LocalExtendedColors.current
    var currentDeck by remember { mutableStateOf(decks.random()) }

    ModalScaffold(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .widthIn(max = 440.dp)
                .fillMaxWidth()
                .height(responsiveModalHeight(300.dp))
                .padding(horizontal = 24.dp)
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(20.dp)),
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = hapticClick(onDismiss),
                    )
                    .padding(4.dp),
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
            Box(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .size(72.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                    .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = if (currentDeck.customCategoryName != null) Icons.Filled.Category else currentDeck.category.iconVector,
                    contentDescription = currentDeck.customCategoryName ?: currentDeck.category.displayName,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp),
                )
            }
            Text(
                text = currentDeck.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 16.dp),
            )
            Text(
                text = currentDeck.shortDescription,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            )

            Spacer(modifier = Modifier.weight(0.5f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .background(Color(0xFF1D293D), RoundedCornerShape(extended.cardCornerRadius))
                        .border(1.dp, Color(0xFF1D293D), RoundedCornerShape(extended.cardCornerRadius))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = hapticClick { currentDeck = decks.randomExcluding(currentDeck) },
                        ),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Shuffle,
                        contentDescription = null,
                        tint = Color(0xFFE2E8F0),
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Shuffle", color = Color(0xFFE2E8F0), style = MaterialTheme.typography.labelLarge)
                }
                Button(
                    onClick = hapticClick { onPlayDeck(currentDeck) },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(extended.cardCornerRadius),
                ) {
                    Text("Play")
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                }
            }
            }
        }
    }
}
