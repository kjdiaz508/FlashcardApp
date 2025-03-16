package dev.kjdz.flashcardapp.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardTopAppBar(
    title: String = "",
    onUpClick: () -> Unit = {},
    actions: @Composable() (RowScope.() -> Unit) = {}
) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            IconButton(onClick = onUpClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Go Back")
            }
        },
        actions = actions,
    )
}