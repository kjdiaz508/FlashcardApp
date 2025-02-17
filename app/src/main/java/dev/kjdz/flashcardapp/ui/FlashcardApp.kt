package dev.kjdz.flashcardapp.ui

import MainScreen
import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import dev.kjdz.flashcardapp.ui.components.BottomNavigationBar
import kotlinx.serialization.Serializable

@Composable
fun FlashcardAppNavHost(navController: NavHostController) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    val shouldShowBottomBar =
        MainScreen.entries.any { item ->
            currentRoute?.endsWith(item.route.toString()) == true
        }

    Scaffold (
        bottomBar = {
            if (shouldShowBottomBar) {
                BottomNavigationBar(
                    selectedRoute = currentRoute,
                    navController = navController,
                )
            }
        }
    ){ paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Routes.Home,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable<Routes.Home> {
                Text("Home Screen")
            }
            composable<Routes.Deck> {
                Text("Deck")
            }
            composable<Routes.Review> {
                Text("Review")
            }
            composable<Routes.Create> {
                Text("Create")
            }
            composable<Routes.Settings> {
                Text("Settings")
            }
        }
    }
}