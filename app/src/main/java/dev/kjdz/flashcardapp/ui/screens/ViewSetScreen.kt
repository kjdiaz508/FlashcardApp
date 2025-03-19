package dev.kjdz.flashcardapp.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import dev.kjdz.flashcardapp.data.CardSet
import dev.kjdz.flashcardapp.ui.components.BottomNavigationBar
import dev.kjdz.flashcardapp.ui.components.BottomOutlineTextField
import dev.kjdz.flashcardapp.ui.components.FlashcardTopAppBar
import dev.kjdz.flashcardapp.ui.navigation.Routes
import dev.kjdz.flashcardapp.ui.viewmodels.FlashcardUiState
import dev.kjdz.flashcardapp.ui.viewmodels.ViewSetViewModel
import java.io.File

@Composable
fun ViewSetScreen(
    navController: NavController,
    viewModel: ViewSetViewModel = viewModel(factory = ViewSetViewModel.Factory)
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

    Scaffold(
        topBar = {
            FlashcardTopAppBar(
                title = uiState.cardSet.name,
                onUpClick = { navController.navigateUp() },
                actions = {
                    if (uiState.isEditing) {
                        IconButton(
                            onClick = { viewModel.saveChanges() },
                            enabled = uiState.cardSet.name.isNotBlank()
                        ) {
                            Icon(Icons.Default.Check, contentDescription = "Save")
                        }
                    } else {
                        IconButton({ viewModel.toggleEditMode() }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (uiState.isEditing) {
                FloatingActionButton({ viewModel.addCard() }) {
                    Icon(Icons.Default.Add, "Add Card")
                }
            }
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
                    cardSet = uiState.cardSet,
                    isEditing = uiState.isEditing,
                    onNameChange = viewModel::updateCardSetName,
                    onDescChange = viewModel::updateCardSetDescription,
                    onSelectImage = {
                        viewModel.setImagePickerCallback { uri ->
                            viewModel.updateCardSetImageUri(uri?.toString())
                        }
                        viewModel.setShowImageDialog(true)
                    },
                )
            }

            item {
                if (!uiState.isEditing) {
                    Button(
                        onClick = { navController.navigate(Routes.Review(uiState.cardSet.id)) },
                        Modifier.fillMaxWidth()
                    ) {
                        Text("Study Set")
                    }
                }
            }

            item { Text("Flashcards (${uiState.cards.size})", style = MaterialTheme.typography.titleMedium) }

            itemsIndexed(uiState.cards) { index, card ->
                FlashcardItem(
                    index = index,
                    card = card,
                    isEditing = uiState.isEditing,
                    onRemove = { viewModel.removeCard(index) },
                    onFrontChange = { viewModel.updateCardFrontText(index, it) },
                    onBackChange = { viewModel.updateCardBackText(index, it) },
                    onSelectImage = {
                        viewModel.setImagePickerCallback { uri ->
                            viewModel.updateCardImageUri(index, uri?.toString())
                        }
                        viewModel.setShowImageDialog(true)
                    },
                )
            }
            item {
                Spacer(Modifier.height(72.dp))
            }
        }
    }
}

@Composable
private fun CardSetInfoSection(
    cardSet: CardSet,
    isEditing: Boolean,
    onNameChange: (String) -> Unit,
    onDescChange: (String) -> Unit,
    onSelectImage: () -> Unit,
) {
    Column(Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(120.dp)
                    .clickable(enabled = isEditing) {
                        onSelectImage()
                    }
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                if (cardSet.imageUri != null) {
                    AsyncImage(cardSet.imageUri, "Card Set Image", modifier = Modifier.fillMaxSize())
                } else {
                    Icon(Icons.Default.Image, "Set Image", Modifier.size(30.dp))
                }
            }
            BottomOutlineTextField(
                value = cardSet.name,
                onValueChange = onNameChange,
                label = "Set Name",
                enabled = isEditing,
                modifier = Modifier.weight(1f)
            )
        }
        BottomOutlineTextField(
            value = cardSet.description ?: "",
            onValueChange = onDescChange,
            label = "Description",
            enabled = isEditing
        )
    }
}

@Composable
private fun FlashcardItem(
    index: Int,
    card: FlashcardUiState,
    isEditing: Boolean,
    onRemove: () -> Unit,
    onFrontChange: (String) -> Unit,
    onBackChange: (String) -> Unit,
    onSelectImage: () -> Unit,
) {
    Card(modifier = Modifier
        .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(Modifier.padding(8.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Card ${index + 1}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary)
                if (isEditing) {
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
            }
            Text(
                text = "Front Content",
                style = MaterialTheme.typography.labelMedium,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(80.dp)
                        .clickable(enabled = isEditing) {
                            onSelectImage()
                        },
                    contentAlignment = Alignment.Center
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
                    onValueChange = onFrontChange,
                    label = "Front",
                    enabled = isEditing
                )
            }
            Text(
                text = "Back Content",
                style = MaterialTheme.typography.labelMedium,
            )
            BottomOutlineTextField(
                value = card.backText,
                onValueChange = onBackChange,
                label = "Back",
                enabled = isEditing
            )
        }
    }
}