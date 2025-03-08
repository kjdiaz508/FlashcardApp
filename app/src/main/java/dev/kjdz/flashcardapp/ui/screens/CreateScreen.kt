package dev.kjdz.flashcardapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import dev.kjdz.flashcardapp.ui.components.BottomNavigationBar
import dev.kjdz.flashcardapp.ui.components.FlashcardTopAppBar
import dev.kjdz.flashcardapp.ui.navigation.Routes
import dev.kjdz.flashcardapp.ui.viewmodels.CreateViewModel
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.tooling.preview.Preview
import dev.kjdz.flashcardapp.ui.theme.FlashcardAppTheme
import dev.kjdz.flashcardapp.ui.viewmodels.FlashcardUiState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CreateScreen(
    viewModel: CreateViewModel = viewModel(factory = CreateViewModel.Factory),
    navController: NavController,
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            FlashcardTopAppBar(
                title = "Create New Set",
                onUpClick = { navController.navigateUp()  },
            )
        },
        bottomBar = {
            BottomNavigationBar(
                selectedRoute = Routes.Create.toString(),
                navController = navController,
            )
        },
        floatingActionButton = {
            FloatingAddCardButton(onClick = { viewModel.addCard() })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(18.dp)
        ) {
            CardSetInfoSection(
                cardSetName = uiState.cardSetName,
                cardSetDescription = uiState.cardSetDescription,
                onCardSetNameChange = viewModel::updateCardSetName,
                onCardSetDescriptionChange = viewModel::updateCardSetDescription
            )

            Spacer(modifier = Modifier.height(24.dp))

            SaveCardSetButton(
                enabled = uiState.cardSetName.isNotBlank(),
                onClick = { viewModel.saveCardSet() }
            )

            Spacer(modifier = Modifier.height(32.dp))

            FlashcardsSection(
                cards = uiState.cards,
                onRemoveCard = { index -> viewModel.removeCard(index) },
                onUpdateFront = { index, text -> viewModel.updateCardFrontText(index, text) },
                onUpdateBack = { index, text -> viewModel.updateCardBackText(index, text) }
            )
        }
    }
}

@Composable
fun CardSetInfoSection(
    cardSetName: String,
    cardSetDescription: String,
    onCardSetNameChange: (String) -> Unit,
    onCardSetDescriptionChange: (String) -> Unit,
) {
    Text(
        text = "Set Information",
        style = MaterialTheme.typography.headlineSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    OutlinedTextField(
        value = cardSetName,
        onValueChange = onCardSetNameChange,
        label = { Text("Set Name *") },
        modifier = Modifier.fillMaxWidth(),
        leadingIcon = {
            Icon(Icons.Default.DriveFileRenameOutline, contentDescription = "Set name")
        },
        isError = cardSetName.isBlank(),
        supportingText = {
            if (cardSetName.isBlank()) {
                Text("Required field")
            }
        }
    )

    Spacer(modifier = Modifier.height(16.dp))

    OutlinedTextField(
        value = cardSetDescription,
        onValueChange = onCardSetDescriptionChange,
        label = { Text("Description") },
        modifier = Modifier.fillMaxWidth(),
        leadingIcon = {
            Icon(Icons.Default.Description, contentDescription = "Description")
        },
        placeholder = { Text("Enter a brief description for your set") },
        singleLine = false,
        minLines = 3
    )
}

@Preview(showBackground = true)
@Composable
fun CardSetInfoPreview() {
    FlashcardAppTheme {
        Column {
            CardSetInfoSection(
                cardSetName = "Wowaweewa",
                cardSetDescription = "stuff here",
                onCardSetNameChange = {},
                onCardSetDescriptionChange = {},
            )
        }
    }
}

@Composable
fun SaveCardSetButton(
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(34.dp),
        enabled = enabled,
    ) {
        Text("Save set", style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun FlashcardsSection(
    cards: List<FlashcardUiState>, // Replace YourCardType with your actual card type
    onRemoveCard: (Int) -> Unit,
    onUpdateFront: (Int, String) -> Unit,
    onUpdateBack: (Int, String) -> Unit,
) {
    Text(
        text = "Flashcards (${cards.size})",
        style = MaterialTheme.typography.headlineSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 16.dp)
    )

    if (cards.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No cards added yet. Tap the '+' button below to start!",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(cards) { index, card ->
                FlashcardCard(
                    index = index,
                    card = card,
                    onRemove = { onRemoveCard(index) },
                    onUpdateFront = { text -> onUpdateFront(index, text) },
                    onUpdateBack = { text -> onUpdateBack(index, text) },
                    modifier = Modifier.animateItem(fadeInSpec = null, fadeOutSpec = null)
                )
            }
        }
    }
}

@Composable
fun FlashcardCard(
    index: Int,
    card: FlashcardUiState, // Replace YourCardType with your actual card type
    onRemove: () -> Unit,
    onUpdateFront: (String) -> Unit,
    onUpdateBack: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Card ${index + 1}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove card",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = card.frontText,
                onValueChange = onUpdateFront,
                label = { Text("Front Content") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Enter question/prompt") },
                leadingIcon = {
                    Icon(Icons.Default.QuestionAnswer, contentDescription = "Front content")
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = card.backText,
                onValueChange = onUpdateBack,
                label = { Text("Back Content") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Enter answer/explanation") },
                leadingIcon = {
                    Icon(Icons.Default.AllInbox, contentDescription = "Back content")
                }
            )
        }
    }
}

@Composable
fun FloatingAddCardButton(onClick: () -> Unit) {
    ExtendedFloatingActionButton(
        onClick = onClick,
        icon = {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Card"
            )
        },
        text = { Text("Add Card") },
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    )
}
