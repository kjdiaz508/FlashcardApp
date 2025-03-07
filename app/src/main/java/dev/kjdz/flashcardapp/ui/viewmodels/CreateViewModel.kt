package dev.kjdz.flashcardapp.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.kjdz.flashcardapp.data.Deck
import dev.kjdz.flashcardapp.data.FlashcardRepository
import dev.kjdz.flashcardapp.FlashcardApplication
import kotlinx.coroutines.launch

class CreateViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: FlashcardRepository
) : ViewModel() {

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as FlashcardApplication)
                CreateViewModel(
                    this.createSavedStateHandle(),
                    application.flashcardRepository
                )
            }
        }
    }

    var deckName by mutableStateOf("")
        private set

    fun onDeckNameChange(newName: String) {
        deckName = newName
    }

    fun addDeck() {
        if (deckName.isNotBlank()) {
            viewModelScope.launch {
                repository.insertDeck(Deck(name = deckName))
            }
            deckName = "" // Clear the text field after saving.
        }
    }
}
