package dev.kjdz.flashcardapp.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Deck::class, Flashcard::class], version = 1)
abstract class FlashcardDatabase : RoomDatabase() {
    abstract fun deckDao(): DeckDao
    abstract fun flashcardDao(): FlashcardDao
}
