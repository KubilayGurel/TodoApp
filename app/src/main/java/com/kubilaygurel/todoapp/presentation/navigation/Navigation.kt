import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kubilaygurel.todoapp.presentation.screens.seconddetail.SecondDetailScreen


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NoteNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(navController = navController)
        }
        composable(
            route = "detail/{noteId}",
            arguments = listOf(navArgument("noteId") { type = NavType.IntType })
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getInt("noteId") ?: 0
            DetailScreen(noteId = noteId, navController = navController)
        }
        composable("completed") {
            CompletedScreen(navController = navController)
        }
        composable(
            route = "secondDetail/{taskId}",
            arguments = listOf(navArgument("taskId") { type = NavType.IntType })
        ) { _ ->
            SecondDetailScreen(
                navController = navController
            )
        }

       }
    }