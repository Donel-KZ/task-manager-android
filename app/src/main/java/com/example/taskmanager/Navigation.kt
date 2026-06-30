package com.example.taskmanager

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.taskmanager.classes.*
import com.example.taskmanager.data.AppContainer
import com.example.taskmanager.screens.*
import kotlinx.coroutines.launch
import java.util.UUID

// No hardcoded users, no seed data. The database starts empty; every task, project,
// and member comes from real user input through the UI. Username/display name come
// from whatever the person actually typed on the login/signup screen.
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(appContainer: AppContainer) {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    val taskRepository = appContainer.taskRepository
    val groupProjectRepository = appContainer.groupProjectRepository
    val userPreferencesRepository = appContainer.userPreferencesRepository

    val tasks by taskRepository.allTasks.collectAsState(initial = emptyList())
    val groupProjects by groupProjectRepository.allProjects.collectAsState(initial = emptyList())

    val currentUserProfilePic by userPreferencesRepository.profilePicUri.collectAsState(initial = null)
    // currentUsername is nullable on purpose — screens that need it (Group Project)
    // are only reachable after login, where it will always be set by then.
    val currentUsername by userPreferencesRepository.currentUsername.collectAsState(initial = null)
    val isLoggedIn by userPreferencesRepository.isLoggedIn.collectAsState(initial = false)

    // Route the start destination based on whether a real session exists, so a
    // returning user skips the login screen instead of re-entering credentials
    // every launch.
    val startDestination = if (isLoggedIn) "home" else "login"

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("login") {
            LogInScreen(navController) { email, password ->
                if (email.isNotBlank() && password.isNotBlank()) {
                    // Username comes directly from what the person typed — no fallback identity.
                    scope.launch {
                        userPreferencesRepository.setSession(username = email, displayName = email)
                    }
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            }
        }

        composable("signup") {
            SignUpScreen(navController) { name, email, password ->
                if (name.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                    scope.launch {
                        userPreferencesRepository.setSession(username = email, displayName = name)
                    }
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
                onUpdateProfilePic = { uri ->
                    scope.launch { userPreferencesRepository.setProfilePicUri(uri.toString()) }
                },
                onAddTask = { task -> scope.launch { taskRepository.addTask(task) } },
                onToggleTask = { task -> scope.launch { taskRepository.toggleCompleted(task) } }
            )
        }

        composable("pending") {
            PendingScreens(
                tasks = tasks,
                navController = navController,
                userProfilePicUri = currentUserProfilePic,
                onUpdateProfilePic = { uri ->
                    scope.launch { userPreferencesRepository.setProfilePicUri(uri.toString()) }
                },
                onAddTask = { task -> scope.launch { taskRepository.addTask(task) } },
                onToggleTask = { task -> scope.launch { taskRepository.toggleCompleted(task) } }
            )
        }

        composable("finished") {
            FinishedScreen(
                tasks = tasks,
                navController = navController,
                userProfilePicUri = currentUserProfilePic,
                onUpdateProfilePic = { uri ->
                    scope.launch { userPreferencesRepository.setProfilePicUri(uri.toString()) }
                }
            )
        }

        composable("group_project") {
            // currentUsername is guaranteed non-null here since this screen is only
            // reachable after a successful login/signup, but we guard anyway rather
            // than silently substituting a placeholder.
            val username = currentUsername
            if (username != null) {
                GroupProjectScreen(
                    projects = groupProjects,
                    navController = navController,
                    currentUsername = username,
                    userProfilePicUri = currentUserProfilePic,
                    onUpdateProfilePic = { uri ->
                        scope.launch { userPreferencesRepository.setProfilePicUri(uri.toString()) }
                    },
                    onCreateProject = { project -> scope.launch { groupProjectRepository.upsert(project) } }
                )
            }
        }

        composable("overdue") {
            OverdueScreen(
                tasks = tasks,
                navController = navController,
                userProfilePicUri = currentUserProfilePic,
                onUpdateProfilePic = { uri ->
                    scope.launch { userPreferencesRepository.setProfilePicUri(uri.toString()) }
                }
            )
        }

        composable("settings") {
            SettingsScreen(
                navController = navController,
                userPreferencesRepository = userPreferencesRepository,
                onLogout = {
                    scope.launch {
                        userPreferencesRepository.clearSession()
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(
            route = "project_details/{projectId}",
            arguments = listOf(navArgument("projectId") { type = NavType.StringType })
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId")
            val project = groupProjects.find { it.id == projectId }
            val username = currentUsername
            if (project != null && username != null) {
                GroupProjectDetailScreen(
                    project = project,
                    currentUsername = username,
                    onBack = { navController.popBackStack() },
                    onProjectUpdate = { updated ->
                        scope.launch { groupProjectRepository.upsert(updated) }
                    }
                )
            }
        }
    }
}
