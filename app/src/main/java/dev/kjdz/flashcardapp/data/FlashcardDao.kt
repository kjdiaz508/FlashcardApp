package dev.kjdz.flashcardapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FlashcardDao {
    @Query("SELECT * FROM Flashcard WHERE set_id = :setId")
    fun getFlashcards(setId: Long): Flow<List<Flashcard>>

    @Query("SELECT * FROM Flashcard WHERE id = :id")
    fun getFlashcard(id: Long): Flow<Flashcard?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashcard(flashcard: Flashcard): Long

    @Update
    suspend fun updateFlashcard(flashcard: Flashcard)

    @Delete
    suspend fun deleteFlashcard(flashcard: Flashcard)
}
