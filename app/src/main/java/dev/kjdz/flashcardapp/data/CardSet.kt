package dev.kjdz.flashcardapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CardSet(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

    var name: String,
    var imageUri: String? = null,
    var description: String? = null,
)