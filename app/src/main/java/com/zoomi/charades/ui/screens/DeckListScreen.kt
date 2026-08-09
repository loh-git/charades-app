package com.zoomi.charades.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.LocalIndication
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.zoomi.charades.data.Category
import com.zoomi.charades.data.Deck
import com.zoomi.charades.ui.components.SelectablePill
import com.zoomi.charades.ui.theme.CorrectGreen
import com.zoomi.charades.ui.theme.LocalExtendedColors
import com.zoomi.charades.ui.theme.PassOrange
import com.zoomi.charades.ui.theme.swatchColor
import com.zoomi.charades.ui.viewmodel.DeckFilter
import com.zoomi.charades.ui.viewmodel.DeckListViewModel

@Composable
fun DeckListScreen(
    viewModel: DeckListViewModel,
    onSelectDeck: (Deck) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenStats: () -> Unit,
    onCreateCustomDeck: () -> Unit,
) {
    val extended = LocalExtendedColors.current
    val decks by viewModel.filteredDecks.collectAsState()
    val allDecks by viewModel.allDecks.collectAsState()
    val query by viewModel.searchQuery.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val favoriteDeckIds by viewModel.favoriteDeckIds.collectAsState()
    var shuffleDialogOpen by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            fullWidthItem {
                TopBar(
                    onShuffle = { shuffleDialogOpen = true },
                    onOpenStats = onOpenStats,
                    onOpenSettings = onOpenSettings,
                )
            }
            fullWidthItem { HeroCard() }
            fullWidthItem {
                CategoryFilterRow(selected = selectedFilter, onSelect = viewModel::onFilterSelected)
            }
            fullWidthItem { SearchField(query = query, onQueryChange = viewModel::onSearchQueryChange) }
            fullWidthItem { AddCustomDeckButton(onCreateCustomDeck) }

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
                        onClick = { onSelectDeck(deck) },
                        onToggleFavorite = { viewModel.onToggleFavorite(deck.id) },
                    )
                }
            }
        }

        if (extended.useBottomNav) {
            BottomNavBar(onOpenStats = onOpenStats, onOpenSettings = onOpenSettings)
        }
    }

    if (shuffleDialogOpen && allDecks.isNotEmpty()) {
        ShuffleDeckDialog(
            decks = allDecks,
            onDismiss = { shuffleDialogOpen = false },
            onPlayDeck = { deck ->
                shuffleDialogOpen = false
                onSelectDeck(deck)
            },
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
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (extended.showHeaderTitle) Arrangement.SpaceBetween else Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (extended.showHeaderTitle) {
            Text(
                "Ultimate Charades — Guess It!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false).padding(end = 8.dp),
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TopBarIconButton(
                icon = "🔀",
                contentDescription = "Shuffle a deck",
                onClick = onShuffle,
                spinOnClick = extended.playfulAnimations,
            )
            if (!extended.useBottomNav) {
                TopBarIconButton(icon = "📊", contentDescription = "Stats", onClick = onOpenStats)
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
                        .clickable(onClick = onOpenSettings),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings", tint = Color.Black)
                }
            }
        }
    }
}

@Composable
private fun TopBarIconButton(
    icon: String,
    contentDescription: String,
    onClick: () -> Unit,
    spinOnClick: Boolean = false,
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
            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
            .clickable(onClick = {
                if (spinOnClick) spinTrigger++
                onClick()
            }),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = icon,
            fontSize = 20.sp,
            modifier = if (spinOnClick) Modifier.graphicsLayer { rotationZ = rotation.value } else Modifier,
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
        BottomNavItem(icon = "🗂️", label = "Decks", selected = true, onClick = {})
        BottomNavItem(icon = "📊", label = "Stats", selected = false, onClick = onOpenStats)
        BottomNavItem(icon = "⚙️", label = "Settings", selected = false, onClick = onOpenSettings)
    }
}

@Composable
private fun BottomNavItem(icon: String, label: String, selected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick).padding(horizontal = 16.dp, vertical = 4.dp),
    ) {
        Text(text = icon, fontSize = 22.sp)
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 2.dp),
        )
    }
}

@Composable
private fun HeroCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(20.dp))
            .padding(20.dp),
    ) {
        Text(
            text = buildAnnotatedString {
                append("Tilt phone ")
                withStyle(SpanStyle(color = PassOrange, fontWeight = FontWeight.Bold)) { append("DOWN") }
                append(" to pass, tilt phone ")
                withStyle(SpanStyle(color = CorrectGreen, fontWeight = FontWeight.Bold)) { append("UP") }
                append(" when guessed correctly!")
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun CategoryFilterRow(selected: DeckFilter, onSelect: (DeckFilter) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SelectablePill(
            label = "⭐ Favourites",
            selected = selected == DeckFilter.Favourites,
            onClick = { onSelect(DeckFilter.Favourites) },
        )
        SelectablePill(label = "🔥 All Decks", selected = selected == DeckFilter.All, onClick = { onSelect(DeckFilter.All) })
        Category.entries.forEach { category ->
            SelectablePill(
                label = "${category.icon} ${category.displayName}",
                selected = selected == DeckFilter.ByCategory(category),
                onClick = { onSelect(DeckFilter.ByCategory(category)) },
            )
        }
    }
}

@Composable
private fun SearchField(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Search decks (e.g., movies, animals, songs)...") },
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
    )
}

@Composable
private fun AddCustomDeckButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = Color.Black),
    ) {
        Text(text = "+  Create Custom Deck")
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
            .clickable(interactionSource = interactionSource, indication = LocalIndication.current, onClick = onClick),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(extended.deckIconSize)
                    .background(deck.category.swatchColor, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = deck.icon, fontSize = (extended.deckIconSize.value * 0.46f).sp)
            }
            Text(
                text = deck.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).padding(top = 10.dp),
            )
        }
        Text(
            text = if (isFavorited) "★" else "☆",
            fontSize = 20.sp,
            color = if (isFavorited) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = LocalIndication.current,
                    onClick = onToggleFavorite,
                )
                .padding(8.dp),
        )
    }
}

@Composable
private fun ShuffleDeckDialog(decks: List<Deck>, onDismiss: () -> Unit, onPlayDeck: (Deck) -> Unit) {
    var currentDeck by remember { mutableStateOf(decks.random()) }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(20.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "🔀 Deck Shuffle",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Box(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .size(72.dp)
                    .background(currentDeck.category.swatchColor, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = currentDeck.icon, fontSize = 34.sp)
            }
            Text(
                text = currentDeck.category.displayName.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp),
            )
            Text(
                text = currentDeck.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp),
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedButton(
                    onClick = { currentDeck = decks.random() },
                    modifier = Modifier.weight(1f),
                ) {
                    Text("🔀 Shuffle")
                }
                Button(
                    onClick = { onPlayDeck(currentDeck) },
                    modifier = Modifier.weight(1f),
                ) {
                    Text("▶  Play")
                }
            }
        }
    }
}
