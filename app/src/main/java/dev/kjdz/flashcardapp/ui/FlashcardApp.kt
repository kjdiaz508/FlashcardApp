package dev.kjdz.flashcardapp.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.kjdz.flashcardapp.ui.screens.CreateScreen
import dev.kjdz.flashcardapp.ui.screens.HomeScreen
import dev.kjdz.flashcardapp.ui.screens.SettingsScreen
import dev.kjdz.flashcardapp.ui.theme.FlashcardAppTheme


@Composable
fun FlashcardApp() {
    val navController = rememberNavController()

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    NavHost(
        navController = navController,
        startDestination = Routes.Home,
    ) {
        composable<Routes.Home> {
            HomeScreen(navController = navController)
        }
        composable<Routes.Deck> {
            Text("Deck")
        }
        composable<Routes.Review> {
            Text("Review")
        }
        composable<Routes.Create> {
            CreateScreen(navController = navController)
        }
        composable<Routes.Settings> {
            SettingsScreen(navController = navController)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StudyHelperAppPreview() {
    FlashcardAppTheme {
        FlashcardApp()
    }
}