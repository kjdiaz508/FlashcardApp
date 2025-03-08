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

    private val cardSetDao = database.cardSetDao()
    private val flashcardDao = database.flashcardDao()

    fun getCardSet(cardSetId: Long) = cardSetDao.getCardSet(cardSetId)

    fun getCardSets() = cardSetDao.getAllCardSets()

    suspend fun createCardSet(cardSet: CardSet): Long {
        val id = cardSetDao.createCardSet(cardSet)
        cardSet.id = id
        return id
    }

    suspend fun deleteCardSet(cardSet: CardSet) {
        cardSetDao.deleteCardSet(cardSet)
    }

    fun getFlashcard(flashcardId: Long) = flashcardDao.getFlashcard(flashcardId)

    fun getFlashcards(cardSetId: Long) = flashcardDao.getFlashcards(cardSetId)

    suspend fun insertFlashcard(flashcard: Flashcard): Long {
        val id = flashcardDao.insertFlashcard(flashcard)
        flashcard.id = id
        return id
    }

    suspend fun updateFlashcard(flashcard: Flashcard) {
        flashcardDao.updateFlashcard(flashcard)
    }

    suspend fun deleteFlashcard(flashcard: Flashcard) {
        flashcardDao.deleteFlashcard(flashcard)
    }

    private suspend fun addTestData() {
        var cardSetId = cardSetDao.createCardSet(CardSet(name = "Test CardSet 1"))
        flashcardDao.insertFlashcard(
            flashcard = Flashcard(
                frontText = "cardSet 1 flashcard 1 front text",
                backText = "cardSet 1 flashcard 1 back text!!!",
                setId = cardSetId,
            )
        )
        flashcardDao.insertFlashcard(
            flashcard = Flashcard(
                frontText = "cardSet 1 flashcard 2222 front text",
                backText = "cardSet 1 flashcard 22222 back text!!!",
                setId = cardSetId,
            )
        )
        flashcardDao.insertFlashcard(
            flashcard = Flashcard(
                frontText = "d1 flashcard 3 front text",
                backText = "d1 flashcard 3 back text!!!",
                setId = cardSetId,
            )
        )

        cardSetId = cardSetDao.createCardSet(CardSet(name = "Test D2"))
        flashcardDao.insertFlashcard(
            flashcard = Flashcard(
                frontText = "scooby dooby doo",
                backText = "where are you",
                setId = cardSetId,
            )
        )

        cardSetDao.createCardSet(CardSet(name = "Chinese"))
        cardSetDao.createCardSet(CardSet(name = "Biology"))
        cardSetDao.createCardSet(CardSet(name = "Theater 212"))
    }
}
