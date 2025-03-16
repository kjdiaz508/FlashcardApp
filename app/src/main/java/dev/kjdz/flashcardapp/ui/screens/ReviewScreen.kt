package dev.kjdz.flashcardapp.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import dev.kjdz.flashcardapp.ui.components.FlashcardTopAppBar
import dev.kjdz.flashcardapp.ui.viewmodels.FlashcardUiState
import dev.kjdz.flashcardapp.ui.viewmodels.ReviewViewModel
import dev.kjdz.flashcardapp.ui.viewmodels.ViewSetViewModel

@Composable
fun ReviewScreen(
    navController: NavController,
    viewModel: ReviewViewModel = viewModel(factory = ReviewViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val flashcards = uiState.flashcards
    val flippedCards = uiState.flippedCards

    Scaffold(
        topBar = {
            FlashcardTopAppBar(
                title = "Studying ${uiState.cardSetName}",
                onUpClick = { navController.navigateUp() }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(flashcards) { index, flashcard ->
                val isFlipped = flippedCards[flashcard.id] ?: false
                FlashcardCard(
                    flashcard = flashcard,
                    isFlipped = isFlipped,
                    position = index + 1,
                    total = flashcards.size,
                    onFlip = { viewModel.toggleCardFlip(flashcard.id) }
                )
            }
        }
    }
}

@Composable
fun FlashcardCard(
    flashcard: FlashcardUiState,
    isFlipped: Boolean,
    position: Int,
    total: Int,
    onFlip: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onFlip()
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Show progress indicator at the top
            Text(
                text = "Card $position of $total",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))
            if (!isFlipped){
                Box(){
                    if (flashcard.cardImageUri != null) {
                        AsyncImage(
                            model = flashcard.cardImageUri,
                            contentDescription = "Flashcard Image",
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "Select card image",
                            modifier = Modifier.size(80.dp)
                        )
                    }
                }
                Text(text = flashcard.frontText, style = MaterialTheme.typography.bodyLarge)
            } else {
                Text(text = flashcard.backText, style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onFlip,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(if (isFlipped) "Flip Back" else "Reveal")
            }
        }
    }
}
