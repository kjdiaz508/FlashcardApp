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
interface CardSetDao {
    @Query("SELECT * FROM CardSet")
    fun getAllCardSets(): Flow<List<CardSet>>

    @Query("SELECT * FROM CardSet WHERE id = :id")
    fun getCardSet(id: Long): Flow<CardSet?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createCardSet(cardSet: CardSet): Long

    @Update
    suspend fun updateCardSet(cardSet: CardSet)

    @Delete
    suspend fun deleteCardSet(cardSet: CardSet)
}