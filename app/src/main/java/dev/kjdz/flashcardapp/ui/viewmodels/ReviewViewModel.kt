package dev.kjdz.flashcardapp.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.toRoute
import dev.kjdz.flashcardapp.FlashcardApplication
import dev.kjdz.flashcardapp.data.FlashcardRepository
import dev.kjdz.flashcardapp.ui.navigation.Routes
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ReviewViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: FlashcardRepository
) : ViewModel() {

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as FlashcardApplication)
                ReviewViewModel(
                    savedStateHandle = createSavedStateHandle(),
                    repository = application.flashcardRepository
                )
            }
        }
    }

    private val cardSetId: Long = savedStateHandle.toRoute<Routes.Review>().setId

    private val _uiState = MutableStateFlow(StudyDeckUiState())
    val uiState: StateFlow<StudyDeckUiState> = _uiState.asStateFlow()

    init {
        loadFlashcards()
    }

    private fun loadCardSet() {
        viewModelScope.launch {
            repository.getCardSet(cardSetId).collect() { cardSet ->
                _uiState.value = _uiState.value.copy(
                    cardSetName = cardSet?.name ?: "Set"
                )
            }
        }
    }

    private fun loadFlashcards() {
        viewModelScope.launch {
            repository.getFlashcards(cardSetId).collect { flashcards ->
                _uiState.value = _uiState.value.copy(
                    flashcards = flashcards.map { it.toUiState() }
                )
            }
        }
    }

    fun toggleCardFlip(cardId: Long) {
        val flippedCards = _uiState.value.flippedCards.toMutableMap()
        flippedCards[cardId] = !(flippedCards[cardId] ?: false)
        _uiState.value = _uiState.value.copy(flippedCards = flippedCards)
    }
}

data class StudyDeckUiState(
    val cardSetName: String = "",
    val flashcards: List<FlashcardUiState> = emptyList(),
    val flippedCards: Map<Long, Boolean> = emptyMap()
)

private fun dev.kjdz.flashcardapp.data.Flashcard.toUiState() = FlashcardUiState(
    id = id,
    frontText = frontText,
    backText = backText,
    cardImageUri = imageUri
)
