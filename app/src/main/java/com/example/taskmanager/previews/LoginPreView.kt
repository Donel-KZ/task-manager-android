package com.example.taskmanager.previews

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.taskmanager.classes.Priority
import com.example.taskmanager.classes.Tasks
import com.example.taskmanager.screens.FinishedScreen
import com.example.taskmanager.screens.LogInScreen

@Preview(showBackground = true)
@Composable
fun LogInScreenPreview() {
    LogInScreen(
        navController = rememberNavController(),
        onLogInClick = { _, _ -> }
    )
}

@Preview(showBackground = true)
@Composable
fun FinishedScreenPreview() {
    FinishedScreen(
        tasks = listOf(
            Tasks(
                id = 1,
                title = "Task 1",
                description = "Description 1",
                completed = true,
                priority = Priority.HIGH,
                dueDate = "12/12/2023"
            )
        ),
        navController = rememberNavController(),
        userProfilePicUri = null,
        onUpdateProfilePic = { }
    )
}
