package dev.kjdz.flashcardapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.kjdz.flashcardapp.FlashcardApplication
import dev.kjdz.flashcardapp.data.FlashcardRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import androidx.lifecycle.viewModelScope
import dev.kjdz.flashcardapp.data.CardSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: FlashcardRepository
) : ViewModel() {

    val cardSets: StateFlow<List<CardSet>> = repository.getCardSets()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun deleteCardSet(cardSet: CardSet) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteCardSet(cardSet)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as FlashcardApplication)
                HomeViewModel(application.flashcardRepository)
            }
        }
    }
}