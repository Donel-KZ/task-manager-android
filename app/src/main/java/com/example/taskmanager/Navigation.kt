package com.example.taskmanager

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.taskmanager.screens.HomeScreen
import com.example.taskmanager.screens.PendingScreens

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {

        composable("home") {
            HomeScreen(navController)
        }

        composable("pending") {
            PendingScreens(navController)
        }

        /*composable("finished") {
            FinishedScreen(navController)
        }*/
    }
}