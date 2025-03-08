package dev.kjdz.flashcardapp.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CardSet::class, Flashcard::class], version = 2)
abstract class FlashcardDatabase : RoomDatabase() {
    abstract fun cardSetDao(): CardSetDao
    abstract fun flashcardDao(): FlashcardDao
}
