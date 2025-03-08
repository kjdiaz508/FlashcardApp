package dev.kjdz.flashcardapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = CardSet::class,
            parentColumns = ["id"],
            childColumns = ["set_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)

data class Flashcard(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

    var frontText: String,
    var backText: String,

    @ColumnInfo(name = "set_id")
    var setId: Long,

    var imageUri: String? = null,
)
