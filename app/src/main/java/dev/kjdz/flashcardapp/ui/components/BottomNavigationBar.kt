package dev.kjdz.flashcardapp.ui.components

import dev.kjdz.flashcardapp.ui.navigation.MainScreen
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

import androidx.navigation.NavController


@Composable
fun BottomNavigationBar(
    selectedRoute: String?,
    navController: NavController
) {
    NavigationBar {
        MainScreen.entries.forEach { item ->
            NavigationBarItem(
                selected = selectedRoute?.endsWith(item.route.toString()) == true,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId)
                    }
                },
                icon = {
                    Icon(item.icon, contentDescription = item.title)
                },
                label = {
                    Text(item.title)
                }
            )
        }
    }
}
