package com.jobtracker.android.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.jobtracker.android.ui.screen.AddEditApplicationScreen
import com.jobtracker.android.ui.screen.ApplicationDetailScreen
import com.jobtracker.android.ui.screen.ApplicationsListScreen
import com.jobtracker.android.ui.screen.SettingsScreen

sealed class Screen(val route: String) {
    object ApplicationsList : Screen("applications_list")
    object AddApplication : Screen("add_application")
    data class EditApplication(val id: String) : Screen("edit_application/$id") {
        companion object {
            const val routeWithArg = "edit_application/{id}"
        }
    }
    data class ApplicationDetail(val id: String) : Screen("application_detail/$id") {
        companion object {
            const val routeWithArg = "application_detail/{id}"
        }
    }
    object Settings : Screen("settings")
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.ApplicationsList.route
    ) {
        composable(Screen.ApplicationsList.route) {
            ApplicationsListScreen(
                onNavigateToDetail = { id ->
                    navController.navigate(Screen.ApplicationDetail(id).route)
                },
                onNavigateToAdd = {
                    navController.navigate(Screen.AddApplication.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.AddApplication.route) {
            AddEditApplicationScreen(
                applicationId = null,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Screen.EditApplication.routeWithArg,
            arguments = listOf(
                androidx.navigation.navArgument("id") { type = androidx.navigation.NavType.StringType }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: return@composable
            AddEditApplicationScreen(
                applicationId = id,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Screen.ApplicationDetail.routeWithArg,
            arguments = listOf(
                androidx.navigation.navArgument("id") { type = androidx.navigation.NavType.StringType }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: return@composable
            ApplicationDetailScreen(
                applicationId = id,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { editId ->
                    navController.navigate(Screen.EditApplication(editId).route)
                }
            )
        }
    }
}

