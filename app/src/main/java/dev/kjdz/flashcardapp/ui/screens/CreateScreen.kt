package dev.kjdz.flashcardapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import dev.kjdz.flashcardapp.ui.components.BottomNavigationBar
import dev.kjdz.flashcardapp.ui.viewmodels.CreateViewModel

@Composable
fun CreateScreen(
    viewModel: CreateViewModel = viewModel(factory = CreateViewModel.Factory),
    navController: NavController,
    onUpClick: () -> Unit = {},
    onSaveClick: () -> Unit = {}
) {
    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedRoute = Routes.Create.toString(),
                navController = navController,
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.addDeck()
                    onSaveClick()
                }
            ) {
                Icon(Icons.Filled.Done, contentDescription = "Save")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .padding(16.dp)) {
            OutlinedTextField(
                value = viewModel.deckName,
                onValueChange = { viewModel.onDeckNameChange(it) },
                label = { Text("Deck Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )
        }
    }
}

@Preview
@Composable
fun PreviewCreateScreen() {
    val navController = rememberNavController()
    CreateScreen(
        navController = navController
    )
}