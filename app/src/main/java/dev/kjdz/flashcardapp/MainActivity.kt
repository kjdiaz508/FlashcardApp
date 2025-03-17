package dev.kjdz.flashcardapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import dev.kjdz.flashcardapp.data.SettingsRepository
import dev.kjdz.flashcardapp.ui.FlashcardApp
import dev.kjdz.flashcardapp.ui.theme.FlashcardAppTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val store = SettingsRepository(applicationContext)
        enableEdgeToEdge()
        setContent {
            val isDarkMode by store.isDarkMode.collectAsState(
                initial = runBlocking { store.isDarkMode.first() }
            )
            FlashcardAppTheme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FlashcardApp()
                }
            }
        }
    }
}

