package com.example.taskmanager.previews

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.taskmanager.screens.PendingScreens

@Preview(showBackground = true)
@Composable
fun pendingProject(){
    PendingScreens(
        navController = rememberNavController()
    )
}