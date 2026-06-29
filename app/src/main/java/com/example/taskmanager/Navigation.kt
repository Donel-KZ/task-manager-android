package com.example.taskmanager

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.taskmanager.classes.*
import com.example.taskmanager.screens.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Hoisting the state to share it across different screens
    val tasks = remember {
        mutableStateListOf(
            Tasks(
                id = 1,
                title = "Finish Android UI",
                description = "Create Home Screen",
                completed = false,
                priority = Priority.HIGH,
                dueDate = "12/12/2024",
            ),
            Tasks(
                id = 2,
                title = "Connect Backend",
                description = "Integrate Spring Boot API",
                completed = false,
                priority = Priority.MEDIUM,
                dueDate = "15/12/2024",
            )
        )
    }

    val groupProjects = remember {
        mutableStateListOf(
            GroupProject(
                id = "1",
                title = "Project Manager App",
                status = Status.PENDING,
                pastDue = false,
                dueDate = "25/12/2024",
                members = listOf(
                    Member("1", "Donel", "donel_dev", Role.OWNER)
                )
            )
        )
    }

    // Shared user state
    var currentUserProfilePic by remember { mutableStateOf<String?>(null) }
    val currentUsername = "donel_dev"

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LogInScreen(navController) { email, password ->
                if (email.isNotBlank() && password.isNotBlank()) {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            }
        }

        composable("signup") {
            SignUpScreen(navController) { name, email, password ->
                if (name.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                    navController.navigate("home") {
                        popUpTo("signup") { inclusive = true }
                    }
                }
            }
        }

        composable("home") {
            HomeScreen(
                tasks = tasks,
                navController = navController,
                userProfilePicUri = currentUserProfilePic,
                onUpdateProfilePic = { currentUserProfilePic = it.toString() }
            )
        }

        composable("pending") {
            PendingScreens(
                tasks = tasks,
                navController = navController,
                userProfilePicUri = currentUserProfilePic,
                onUpdateProfilePic = { currentUserProfilePic = it.toString() }
            )
        }

        composable("finished") {
            FinishedScreen(
                tasks = tasks,
                navController = navController,
                userProfilePicUri = currentUserProfilePic,
                onUpdateProfilePic = { currentUserProfilePic = it.toString() }
            )
        }

        composable("group_project") {
            GroupProjectScreen(
                projects = groupProjects,
                navController = navController,
                currentUsername = currentUsername,
                userProfilePicUri = currentUserProfilePic,
                onUpdateProfilePic = { currentUserProfilePic = it.toString() }
            )
        }

        composable("overdue") {
            OverdueScreen(
                tasks = tasks,
                navController = navController,
                userProfilePicUri = currentUserProfilePic,
                onUpdateProfilePic = { currentUserProfilePic = it.toString() }
            )
        }

        composable(
            route = "project_details/{projectId}",
            arguments = listOf(navArgument("projectId") { type = NavType.StringType })
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId")
            val project = groupProjects.find { it.id == projectId }
            if (project != null) {
                GroupProjectDetailScreen(
                    project = project,
                    currentUsername = currentUsername,
                    onBack = { navController.popBackStack() },
                    onProjectUpdate = { updatedProject ->
                        val index = groupProjects.indexOfFirst { it.id == updatedProject.id }
                        if (index != -1) {
                            groupProjects[index] = updatedProject
                        }
                    }
                )
            }
        }
    }
}
