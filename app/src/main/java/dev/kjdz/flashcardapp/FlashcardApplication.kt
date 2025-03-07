package dev.kjdz.flashcardapp

import android.app.Application
import dev.kjdz.flashcardapp.data.FlashcardRepository

class FlashcardApplication: Application() {
    lateinit var flashcardRepository: FlashcardRepository
    override fun onCreate() {
        super.onCreate()
        flashcardRepository = FlashcardRepository(this.applicationContext)
    }
}