package dev.kjdz.flashcardapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import dev.kjdz.flashcardapp.data.CardSet
import dev.kjdz.flashcardapp.ui.components.BottomNavigationBar
import dev.kjdz.flashcardapp.ui.navigation.Routes
import dev.kjdz.flashcardapp.ui.viewmodels.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory),
    navController: NavController,
) {
    val cardSets by viewModel.cardSets.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Home") }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                selectedRoute = Routes.Home.toString(),
                navController = navController,
            )
        }
    ) { innerPadding ->
        CardSetGrid(
            cardSets = cardSets,
            innerPadding = innerPadding,
            onCardSetClick = { cardSet ->
                navController.navigate(Routes.ViewSet(setId = cardSet.id))
            },
            onDeleteClick = { cardSet ->
                viewModel.deleteCardSet(cardSet)
            }
        )
    }
}

@Composable
fun CardSetGrid(
    cardSets: List<CardSet>,
    innerPadding: PaddingValues,
    onCardSetClick: (CardSet) -> Unit,
    onDeleteClick: (CardSet) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .padding(innerPadding)
            .padding(8.dp)
    ) {
        items(cardSets) { cardSet ->
            CardSetCard(
                cardSet = cardSet,
                onCardSetClick = { onCardSetClick(cardSet) },
                onDeleteClick = { onDeleteClick(cardSet) }
            )
        }
    }
}

@Composable
fun CardSetCard(
    cardSet: CardSet,
    onCardSetClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onCardSetClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.Center,
            ) {
                if (cardSet.imageUri != null) {
                    AsyncImage(
                        model = cardSet.imageUri,
                        contentDescription = "CardSet Image",
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "Placeholder",
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = cardSet.name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}
