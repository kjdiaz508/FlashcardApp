package dev.kjdz.flashcardapp.ui.components

import MainScreen
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import dev.kjdz.flashcardapp.R

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
