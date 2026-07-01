package com.example.taskmanager

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.taskmanager.classes.*
import com.example.taskmanager.data.AppContainer
import com.example.taskmanager.screens.*
import kotlinx.coroutines.launch

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
    val authRepository = appContainer.authRepository
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
            var authError by remember { mutableStateOf<String?>(null) }
            var authLoading by remember { mutableStateOf(false) }

            LogInScreen(
                navController = navController,
                isLoading = authLoading,
                errorMessage = authError
            ) { email, password ->
                if (email.isNotBlank() && password.isNotBlank()) {
                    scope.launch {
                        authLoading = true
                        authError = null
                        runCatching {
                            authRepository.login(email, password)
                            taskRepository.syncFromBackend()
                            groupProjectRepository.syncFromBackend()
                        }.onSuccess {
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                            }
                        }.onFailure { throwable ->
                            authError = throwable.message ?: "Unable to log in."
                        }
                        authLoading = false
                    }
                }
            }
        }

        composable("signup") {
            var authError by remember { mutableStateOf<String?>(null) }
            var authLoading by remember { mutableStateOf(false) }

            SignUpScreen(
                navController = navController,
                isLoading = authLoading,
                errorMessage = authError
            ) { name, email, password ->
                if (name.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                    scope.launch {
                        authLoading = true
                        authError = null
                        runCatching {
                            authRepository.register(name, email, password)
                            taskRepository.syncFromBackend()
                            groupProjectRepository.syncFromBackend()
                        }.onSuccess {
                            navController.navigate("home") {
                                popUpTo("signup") { inclusive = true }
                            }
                        }.onFailure { throwable ->
                            authError = throwable.message ?: "Unable to create account."
                        }
                        authLoading = false
                    }
                }
            }
        }

        composable("forgot_password") {
            var authError by remember { mutableStateOf<String?>(null) }
            var authLoading by remember { mutableStateOf(false) }
            var successMessage by remember { mutableStateOf<String?>(null) }

            ForgotPasswordScreen(
                navController = navController,
                isLoading = authLoading,
                errorMessage = authError,
                successMessage = successMessage
            ) { email ->
                scope.launch {
                    authLoading = true
                    authError = null
                    successMessage = null
                    runCatching {
                        authRepository.forgotPassword(email)
                    }.onSuccess {
                        successMessage = it
                    }.onFailure { throwable ->
                        authError = throwable.message ?: "Failed to send reset link."
                    }
                    authLoading = false
                }
            }
        }

        composable(
            route = "reset_password?token={token}",
            arguments = listOf(navArgument("token") { type = NavType.StringType; defaultValue = "" }),
            deepLinks = listOf(navDeepLink { uriPattern = "taskmanager://reset-password?token={token}" })
        ) { backStackEntry ->
            val token = backStackEntry.arguments?.getString("token") ?: ""
            var authError by remember { mutableStateOf<String?>(null) }
            var authLoading by remember { mutableStateOf(false) }

            ResetPasswordScreen(
                navController = navController,
                token = token,
                isLoading = authLoading,
                errorMessage = authError
            ) { resetToken, newPassword ->
                scope.launch {
                    authLoading = true
                    authError = null
                    runCatching {
                        authRepository.resetPassword(resetToken, newPassword)
                    }.onSuccess {
                        navController.navigate("login") {
                            popUpTo("login") { inclusive = true }
                        }
                    }.onFailure { throwable ->
                        authError = throwable.message ?: "Failed to reset password."
                    }
                    authLoading = false
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
                    onCreateProject = { project -> groupProjectRepository.upsert(project) }
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
