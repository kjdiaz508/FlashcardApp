package dev.kjdz.flashcardapp.ui.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.kjdz.flashcardapp.FlashcardApplication
import dev.kjdz.flashcardapp.data.CardSet
import dev.kjdz.flashcardapp.data.Flashcard
import dev.kjdz.flashcardapp.data.FlashcardRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CreateViewModel(
    private val repository: FlashcardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateCardSetUiState())
    val uiState: StateFlow<CreateCardSetUiState> = _uiState.asStateFlow()

    /**
     * Update cardSet name in UI state
     */
    fun updateCardSetName(name: String) {
        _uiState.value = _uiState.value.copy(cardSetName = name)
    }

    /**
     * Update cardSet description in UI state
     */
    fun updateCardSetDescription(description: String) {
        _uiState.value = _uiState.value.copy(cardSetDescription = description)
    }

    /**
     * Update cardSet image Uri in UI state (optional if you want images)
     */
    fun updateCardSetImageUri(uri: String?) {
        _uiState.value = _uiState.value.copy(cardSetImageUri = uri)
    }

    /**
     * Add a new card (empty front/back) to the list
     */
    fun addCard() {
        val updatedCards = _uiState.value.cards.toMutableList()
        updatedCards.add(FlashcardUiState()) // blank front/back
        _uiState.value = _uiState.value.copy(cards = updatedCards)
    }

    /**
     * Remove an existing card by index
     */
    fun removeCard(index: Int) {
        val updatedCards = _uiState.value.cards.toMutableList()
        if (index in updatedCards.indices) {
            updatedCards.removeAt(index)
            _uiState.value = _uiState.value.copy(cards = updatedCards)
        }
    }

    /**
     * Update the front text of a card
     */
    fun updateCardFrontText(index: Int, newFront: String) {
        val updatedCards = _uiState.value.cards.toMutableList()
        if (index in updatedCards.indices) {
            val oldCard = updatedCards[index]
            updatedCards[index] = oldCard.copy(frontText = newFront)
            _uiState.value = _uiState.value.copy(cards = updatedCards)
        }
    }

    /**
     * Update the back text of a card
     */
    fun updateCardBackText(index: Int, newBack: String) {
        val updatedCards = _uiState.value.cards.toMutableList()
        if (index in updatedCards.indices) {
            val oldCard = updatedCards[index]
            updatedCards[index] = oldCard.copy(backText = newBack)
            _uiState.value = _uiState.value.copy(cards = updatedCards)
        }
    }

    /**
     * Save the cardSet and its cards to the DB.
     * Make sure your repository has a function that returns the new cardSet ID.
     */
    fun saveCardSet() {
        val currentState = _uiState.value

        // Don’t save an empty name
        if (currentState.cardSetName.isBlank()) {
            // Could show a snackbar, toast, or handle error state
            return
        }

        // Launch a coroutine to perform DB operations
        viewModelScope.launch(Dispatchers.IO) {
            // Create the CardSet in the DB
            val cardSet = CardSet(
                name = currentState.cardSetName,
                imageUri = currentState.cardSetImageUri,
                description = currentState.cardSetDescription
            )
            // This function should return the new cardSet’s ID.
            // Make sure to implement createCardSetAndReturnId in your repository
            val newCardSetId = repository.createCardSet(cardSet)

            // Now insert each flashcard, referencing the new cardSet’s ID
            currentState.cards.forEach { cardUiState ->
                val flashcard = Flashcard(
                    frontText = cardUiState.frontText,
                    backText = cardUiState.backText,
                    setId = newCardSetId,
                    imageUri = cardUiState.cardImageUri
                )
                repository.insertFlashcard(flashcard)
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as FlashcardApplication)
                CreateViewModel(
                    application.flashcardRepository
                )
            }
        }
    }
}

data class CreateCardSetUiState(
    val cardSetName: String = "",
    val cardSetDescription: String = "",
    val cardSetImageUri: String? = null,
    val cards: List<FlashcardUiState> = emptyList()
)

data class FlashcardUiState(
    val id: Long = 0L,
    val frontText: String = "",
    val backText: String = "",
    val cardImageUri: String? = null
)
