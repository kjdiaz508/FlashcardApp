package dev.kjdz.flashcardapp.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.AsyncImage
import dev.kjdz.flashcardapp.ui.components.BottomOutlineTextField
import dev.kjdz.flashcardapp.ui.theme.FlashcardAppTheme
import dev.kjdz.flashcardapp.ui.viewmodels.FlashcardUiState

@Composable
fun CreateScreen(
    viewModel: CreateViewModel = viewModel(factory = CreateViewModel.Factory),
    navController: NavController,
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var imagePickerCallback by remember { mutableStateOf<((Uri?) -> Unit)?>(null) }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
        if (uri != null) {
            context.contentResolver.takePersistableUriPermission(uri, flag)
        }
        imagePickerCallback?.invoke(uri)
        imagePickerCallback = null
    }

    fun launchImagePicker(onResult: (Uri?) -> Unit) {
        imagePickerCallback = onResult
        imagePickerLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    Scaffold(
        topBar = {
            FlashcardTopAppBar(
                title = "Create New Set",
                onUpClick = { navController.navigateUp() },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.saveCardSet()
                            viewModel.clearData()
                        },
                        enabled = uiState.cardSetName.isNotBlank()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Save Set",
                        )
                    }
                }
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                CardSetInfoSection(
                    cardSetName = uiState.cardSetName,
                    cardSetDescription = uiState.cardSetDescription,
                    cardSetImage = uiState.cardSetImageUri,
                    onCardSetNameChange = viewModel::updateCardSetName,
                    onCardSetDescriptionChange = viewModel::updateCardSetDescription,
                    onSelectImage = {
                        launchImagePicker { uri: Uri? ->
                            viewModel.updateCardSetImageUri(uri?.toString())
                        }
                    },
                )
            }
            item { Text(
                text = "Flashcards (${uiState.cards.size})",
                style = MaterialTheme.typography.titleMedium,
            ) }
            if (uiState.cards.isEmpty()){
                item {
                    Text(
                        text = "No cards added yet. Use the '+' button below!",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                itemsIndexed(uiState.cards) { index, card ->
                    Flashcard(
                        index = index,
                        card = card,
                        onRemove = {viewModel.removeCard(index)},
                        onUpdateFront = { text -> viewModel.updateCardFrontText(index, text) },
                        onUpdateBack = { text -> viewModel.updateCardBackText(index, text) },
                        onSelectCardImage = {
                            launchImagePicker { uri: Uri? ->
                                viewModel.updateCardImageUri(index, uri?.toString())
                            }
                        },
                        modifier = Modifier.animateItem(fadeOutSpec = null, fadeInSpec = null)
                    )
                }
            }
            item {
                Spacer(Modifier.height(72.dp))
            }
        }
    }
}

@Composable
fun CardSetInfoSection(
    cardSetName: String,
    cardSetDescription: String,
    cardSetImage: String?,
    onCardSetNameChange: (String) -> Unit,
    onCardSetDescriptionChange: (String) -> Unit,
    onSelectImage: () -> Unit,
) {
    Column(
        Modifier
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier =  Modifier
                    .size(120.dp)
                    .padding(4.dp)
                    .clickable {
                        onSelectImage()
                    },
                contentAlignment = Alignment.Center,
            ) {
                if (cardSetImage != null) {
                    // Display the selected image for the card
                    // Example: Image(painter = rememberAsyncImagePainter(card.cardImageUri), contentDescription = "Card image", modifier = Modifier.fillMaxSize())
                    AsyncImage(
                        model = cardSetImage,
                        contentDescription = "Deck Image",
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "Select card image",
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
            BottomOutlineTextField(
                placeholder = "Subject / Title",
                label = "Set Name *",
                value = cardSetName,
                onValueChange = onCardSetNameChange
            )
        }
        BottomOutlineTextField(
            placeholder = "What is your set about?",
            label = "Description",
            value = cardSetDescription,
            onValueChange = onCardSetDescriptionChange
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CardSetInfoPreview() {
    FlashcardAppTheme {
        Column {
            CardSetInfoSection(
                cardSetName = "Wowaweewa",
                cardSetDescription = "stuff here",
                cardSetImage = null,
                onCardSetNameChange = {},
                onCardSetDescriptionChange = {},
                onSelectImage = {},
            )
        }
    }
}

@Composable
fun Flashcard(
    index: Int,
    card: FlashcardUiState,
    onRemove: () -> Unit,
    onUpdateFront: (String) -> Unit,
    onUpdateBack: (String) -> Unit,
    onSelectCardImage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column (
            Modifier.padding(8.dp)
        ) {
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Card ${index + 1}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove card",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Text(
                text = "Front Content",
                style = MaterialTheme.typography.labelMedium,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier =  Modifier
                        .size(80.dp)
                        .clickable { onSelectCardImage() },
                    contentAlignment = Alignment.Center,
                ) {
                    if (card.cardImageUri != null) {
                        AsyncImage(
                            model = card.cardImageUri,
                            contentDescription = "Flashcard Image",
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "Select card image",
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
                BottomOutlineTextField(
                    value = card.frontText,
                    label = "Front Text",
                    onValueChange = onUpdateFront,
                    placeholder = "Prompt/Question"
                )
            }
            Text(
                text = "Back Content",
                style = MaterialTheme.typography.labelMedium,
            )
            Spacer(Modifier.height(6.dp))
            BottomOutlineTextField(
                value = card.backText,
                label = "Back Text",
                onValueChange = onUpdateBack,
                placeholder = "Answer/Description"
            )
        }
    }
}


@Preview
@Composable
fun FlashcardPreview() {
    FlashcardAppTheme {
        Flashcard(
            index = 1,
            card = FlashcardUiState(
                1,
                "Test Front Text",
                backText = "Test Back Text",
                cardImageUri = null
            ),
            onRemove = {},
            onUpdateFront = {},
            onUpdateBack = {},
            onSelectCardImage = {},
        )
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
