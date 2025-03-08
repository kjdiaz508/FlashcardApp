package dev.kjdz.flashcardapp.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import dev.kjdz.flashcardapp.ui.components.BottomNavigationBar
import dev.kjdz.flashcardapp.ui.navigation.Routes

@Composable
fun SettingsScreen(
    navController: NavController,
) {
    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedRoute = Routes.Settings.toString(),
                navController = navController,
            )
        }
    ) { innerPadding ->
        Text("SettingsScreen", modifier = Modifier.padding(innerPadding))
    }
}

@Preview
@Composable
fun PreviewSettingsScreen() {
    val navController = rememberNavController()
    SettingsScreen(
        navController = navController
    )
}