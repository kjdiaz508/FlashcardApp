package dev.kjdz.flashcardapp.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.toRoute
import dev.kjdz.flashcardapp.FlashcardApplication
import dev.kjdz.flashcardapp.data.CardSet
import dev.kjdz.flashcardapp.data.Flashcard
import dev.kjdz.flashcardapp.data.FlashcardRepository
import dev.kjdz.flashcardapp.ui.navigation.Routes
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ViewSetViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: FlashcardRepository
) : ViewModel() {

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as FlashcardApplication)
                ViewSetViewModel(
                    savedStateHandle = createSavedStateHandle(),
                    repository = application.flashcardRepository
                )
            }
        }
    }

    // Extract card set ID from navigation arguments
    private val cardSetId: Long = savedStateHandle.toRoute<Routes.ViewSet>().setId

    // UI state flows
    private val isEditing = MutableStateFlow(false)
    private val updatedCardSet = MutableStateFlow(CardSet(name = ""))
    private val updatedCards = MutableStateFlow<List<FlashcardUiState>>(emptyList())

    val uiState: StateFlow<ViewSetUiState> = transformedFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = ViewSetUiState(),
        )

    private fun transformedFlow() = combine(
        repository.getCardSet(cardSetId),
        repository.getFlashcards(cardSetId),
        isEditing,
        updatedCardSet,
        updatedCards
    ) { cardSetFlow, flashcards, editing, updatedSet, updatedFlashcards ->
        val cardSet = cardSetFlow ?: CardSet(name = "")

        ViewSetUiState(
            cardSet = if (editing) updatedSet else cardSet,
            cards = if (editing) updatedFlashcards else flashcards.map { it.toUiState() },
            isEditing = editing
        )
    }

    fun toggleEditMode() {
        isEditing.value = !isEditing.value
        if (isEditing.value) {
            // Capture current state from repository data
            viewModelScope.launch {
                val currentSet = repository.getCardSet(cardSetId).first()
                val currentCards = repository.getFlashcards(cardSetId).first()
                    .map { it.toUiState() }

                updatedCardSet.value = currentSet ?: CardSet(name = "")
                updatedCards.value = currentCards
            }
        }
    }

    fun updateCardSetName(name: String) {
        updatedCardSet.value = updatedCardSet.value.copy(name = name)
    }

    fun updateCardSetDescription(description: String) {
        updatedCardSet.value = updatedCardSet.value.copy(description = description)
    }

    fun updateCardSetImageUri(uri: String?) {
        updatedCardSet.value = updatedCardSet.value.copy(imageUri = uri)
    }

    fun addCard() {
        val newCards = updatedCards.value.toMutableList()
        newCards.add(FlashcardUiState())
        updatedCards.value = newCards
    }

    fun removeCard(index: Int) {
        val newCards = updatedCards.value.toMutableList()
        if (index in newCards.indices) {
            newCards.removeAt(index)
            updatedCards.value = newCards
        }
    }

    fun updateCardFrontText(index: Int, text: String) {
        val newCards = updatedCards.value.toMutableList()
        if (index in newCards.indices) {
            newCards[index] = newCards[index].copy(frontText = text)
            updatedCards.value = newCards
        }
    }

    fun updateCardBackText(index: Int, text: String) {
        val newCards = updatedCards.value.toMutableList()
        if (index in newCards.indices) {
            newCards[index] = newCards[index].copy(backText = text)
            updatedCards.value = newCards
        }
    }

    fun updateCardImageUri(index: Int, uri: String?) {
        val newCards = updatedCards.value.toMutableList()
        if (index in newCards.indices) {
            newCards[index] = newCards[index].copy(cardImageUri = uri)
            updatedCards.value = newCards
        }
    }

    fun saveChanges() {
        viewModelScope.launch {
            // Update CardSet
            repository.updateCardSet(updatedCardSet.value)

            // Process flashcards
            val currentCards = updatedCards.value
            val existingCards = repository.getFlashcards(cardSetId).first() // Collect flow

            // Delete removed cards
            existingCards.filter { existing ->
                currentCards.none { it.id == existing.id }
            }.forEach { repository.deleteFlashcard(it) }

            // Update or insert cards
            currentCards.forEach { uiCard ->
                val flashcard = uiCard.toFlashcard(cardSetId)
                if (uiCard.id == 0L) {
                    repository.insertFlashcard(flashcard)
                } else {
                    repository.updateFlashcard(flashcard)
                }
            }

            toggleEditMode()
        }
    }
}

data class ViewSetUiState(
    val cardSet: CardSet = CardSet(name = ""),
    val cards: List<FlashcardUiState> = emptyList(),
    val isEditing: Boolean = false
)

private fun Flashcard.toUiState() = FlashcardUiState(
    id = id,
    frontText = frontText,
    backText = backText,
    cardImageUri = imageUri
)

private fun FlashcardUiState.toFlashcard(setId: Long) = Flashcard(
    id = id,
    frontText = frontText,
    backText = backText,
    setId = setId,
    imageUri = cardImageUri
)
