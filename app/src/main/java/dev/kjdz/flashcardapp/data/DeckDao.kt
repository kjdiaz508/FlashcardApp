package dev.kjdz.flashcardapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import javax.security.auth.Subject

@Dao
interface DeckDao {
    @Query("SELECT * FROM Deck")
    fun getAllDecks(): Flow<List<Deck>>

    @Query("SELECT * FROM Deck WHERE id = :id")
    fun getDeck(id: Long): Flow<Deck?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun createDeck(deck: Deck): Long

    @Update
    fun updateDeck(deck: Deck)

    @Delete
    fun deleteDeck(deck: Deck)
}
