package dev.kjdz.flashcardapp

import android.app.Application
import dev.kjdz.flashcardapp.data.FlashcardRepository
import dev.kjdz.flashcardapp.data.SettingsRepository

class FlashcardApplication: Application() {
    lateinit var flashcardRepository: FlashcardRepository
    lateinit var settingsRepository: SettingsRepository
    override fun onCreate() {
        super.onCreate()
        flashcardRepository = FlashcardRepository(this.applicationContext)
        settingsRepository = SettingsRepository(this.applicationContext)
    }
}