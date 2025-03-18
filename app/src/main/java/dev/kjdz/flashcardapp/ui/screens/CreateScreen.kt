package dev.kjdz.flashcardapp.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.util.Log
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
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil3.compose.AsyncImage
import dev.kjdz.flashcardapp.ui.components.BottomOutlineTextField
import dev.kjdz.flashcardapp.ui.theme.FlashcardAppTheme
import dev.kjdz.flashcardapp.ui.viewmodels.FlashcardUiState
import java.io.File

@Composable
fun CreateScreen(
    viewModel: CreateViewModel = viewModel(factory = CreateViewModel.Factory),
    navController: NavController,
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
        if (uri != null) {
            context.contentResolver.takePersistableUriPermission(uri, flag)
            uiState.imagePickerCallback?.invoke(uri)
        }
        viewModel.setImagePickerCallback(null)
    }


    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            uiState.imagePickerCallback?.invoke(uiState.cameraImageUri)
        }
        viewModel.setImagePickerCallback(null)
        viewModel.setCameraImageUri(null)
    }

    fun createImageUri(context: Context): Uri {
        val filename = "IMG_${System.currentTimeMillis()}.jpg"
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File(storageDir, filename)
        return FileProvider.getUriForFile(
            context,
            "dev.kjdz.flashcardapp.fileprovider",
            file
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // If permission is granted, launch the camera
            viewModel.setCameraImageUri(createImageUri(context))
            uiState.cameraImageUri?.let { cameraLauncher.launch(it) }
        } else {
            Log.e("CreateScreen", "Camera permission denied")
        }
    }

    fun launchImagePicker(onResult: (Uri?) -> Unit) {
        viewModel.setImagePickerCallback(onResult)
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
        if (uiState.showImageSourceDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.setShowImageDialog(false) },
                title = { Text("Choose Image Source") },
                text = { Text("Select camera to take a new photo or gallery to choose an existing one.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.setShowImageDialog(false)
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                                PackageManager.PERMISSION_GRANTED) {
                                // Permission is already granted, launch camera
                                val imageUri = createImageUri(context)
                                viewModel.setCameraImageUri(imageUri)
                                cameraLauncher.launch(imageUri)
                            } else {
                                // Request permission
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        }
                    ) { Text("Take Photo") }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            viewModel.setShowImageDialog(false)
                            imagePickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                    ) { Text("Choose from Gallery") }
                }
            )
        }

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
                        viewModel.setImagePickerCallback { uri ->
                            viewModel.updateCardSetImageUri(uri?.toString())
                        }
                        viewModel.setShowImageDialog(true)
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
                            viewModel.setImagePickerCallback { uri ->
                                viewModel.updateCardImageUri(index, uri?.toString())
                            }
                            viewModel.setShowImageDialog(true)
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
                        contentDescription = "Set Image",
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
