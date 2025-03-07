package dev.kjdz.flashcardapp.ui.screens

import android.view.View
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import dev.kjdz.flashcardapp.ui.components.BottomNavigationBar

@Composable
fun HomeScreen(
    navController: NavController,
) {
    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedRoute = Routes.Home.toString(),
                navController = navController,
            )
        }
    ) { innerPadding ->
        Text("HomeScreen", modifier = Modifier.padding(innerPadding))
    }
}

@Preview
@Composable
fun PreviewHomeScreen() {
    val navController = rememberNavController()
    HomeScreen(
        navController = navController
    )
}