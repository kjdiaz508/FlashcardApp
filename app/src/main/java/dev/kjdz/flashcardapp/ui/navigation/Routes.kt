import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

sealed class Routes {
    @Serializable
    data object Home

    @Serializable
    data object Create

    @Serializable
    data object Settings

    @Serializable
    data class Deck(
        val deckId: Int
    )

    @Serializable
    data class Review(
        val deckId: Int
    )
}

enum class MainScreen(val route: Any, val title: String, val icon: ImageVector) {
    HOME(Routes.Home, "Home", Icons.Default.Home),
    CREATE(Routes.Create, "Create", Icons.Default.Create),
    SETTINGS(Routes.Settings, "Settings", Icons.Default.Settings)
}