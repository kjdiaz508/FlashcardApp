package dev.kjdz.flashcardapp.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.kjdz.flashcardapp.ui.navigation.Routes
import dev.kjdz.flashcardapp.ui.screens.CreateScreen
import dev.kjdz.flashcardapp.ui.screens.HomeScreen
import dev.kjdz.flashcardapp.ui.screens.ReviewScreen
import dev.kjdz.flashcardapp.ui.screens.SettingsScreen
import dev.kjdz.flashcardapp.ui.screens.ViewSetScreen
import dev.kjdz.flashcardapp.ui.viewmodels.CreateViewModel
import dev.kjdz.flashcardapp.ui.viewmodels.HomeViewModel
import dev.kjdz.flashcardapp.ui.viewmodels.ReviewViewModel


@Composable
fun FlashcardApp() {
    val navController = rememberNavController()
    val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
    val createViewModel: CreateViewModel = viewModel(factory = CreateViewModel.Factory)

    NavHost(
        navController = navController,
        startDestination = Routes.Home,
    ) {
        composable<Routes.Home> {
            HomeScreen(homeViewModel, navController)
        }
        composable<Routes.ViewSet> {
            ViewSetScreen(navController = navController)
        }
        composable<Routes.Review> {
            ReviewScreen(navController = navController)
        }
        composable<Routes.Create> {
            CreateScreen(createViewModel, navController)
        }
        composable<Routes.Settings> {
            SettingsScreen(navController = navController)
        }
    }
}