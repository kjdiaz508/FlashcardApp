package dev.kjdz.flashcardapp.data

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class FlashcardRepository(
    context: Context
) {
    private val databaseCallback = object : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            CoroutineScope(Dispatchers.IO).launch {
                addTestData()
            }
        }
    }
    private val database: FlashcardDatabase = Room.databaseBuilder(
        context,
        FlashcardDatabase::class.java,
        "Flashcard.db"
    ).addCallback(databaseCallback).build()

    private val deckDao = database.deckDao()
    private val flashcardDao = database.flashcardDao()

    fun getDeck(deckId: Long) = deckDao.getDeck(deckId)

    fun getDecks() = deckDao.getAllDecks()

    fun createDeck(deck: Deck) {
        if (deck.name.trim() != "") {
            CoroutineScope(Dispatchers.IO).launch {
                deck.id = deckDao.createDeck(deck)
            }
        }
    }

    fun deleteDeck(deck: Deck) {
        CoroutineScope(Dispatchers.IO).launch {
            deckDao.deleteDeck(deck)
        }
    }

    fun getFlashcard(flashcardId: Long) = flashcardDao.getFlashcard(flashcardId)

    fun getFlashcards(deckId: Long) = flashcardDao.getFlashcards(deckId)

    fun insertFlashcard(flashcard: Flashcard) {
        CoroutineScope(Dispatchers.IO).launch {
            flashcard.id = flashcardDao.insertFlashcard(flashcard)
        }
    }

    fun updateFlashcard(flashcard: Flashcard) {
        CoroutineScope(Dispatchers.IO).launch {
            flashcardDao.updateFlashcard(flashcard)
        }
    }

    fun deleteQuestion(flashcard: Flashcard) {
        CoroutineScope(Dispatchers.IO).launch {
            flashcardDao.deleteFlashcard(flashcard)
        }
    }

    private fun addTestData() {
        var deckId = deckDao.createDeck(Deck(name = "Test Deck 1"))
        flashcardDao.insertFlashcard(
            flashcard = Flashcard(
                frontText = "deck 1 flashcard 1 front text",
                backText = "deck 1 flashcard 1 back text!!!",
                deckId = deckId
            )
        )
        flashcardDao.insertFlashcard(
            flashcard = Flashcard(
                frontText = "deck 1 flashcard 2222 front text",
                backText = "deck 1 flashcard 22222 back text!!!",
                deckId = deckId
            )
        )
        flashcardDao.insertFlashcard(
            flashcard = Flashcard(
                frontText = "d1 flashcard 3 front text",
                backText = "d1 flashcard 3 back text!!!",
                deckId = deckId
            )
        )

        deckId = deckDao.createDeck(Deck(name = "Test D2"))
        flashcardDao.insertFlashcard(
            flashcard = Flashcard(
                frontText = "scooby dooby doo",
                backText = "where are you",
                deckId = deckId
            )
        )

        deckDao.createDeck(Deck(name = "Chinese"))
        deckDao.createDeck(Deck(name = "Biology"))
        deckDao.createDeck(Deck(name = "Theater 212"))
    }
}
