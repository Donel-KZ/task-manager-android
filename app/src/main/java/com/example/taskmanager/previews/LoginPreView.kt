package com.example.taskmanager.previews

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.taskmanager.classes.Tasks
import com.example.taskmanager.screens.FinishedScreen
import com.example.taskmanager.screens.LogInScreen
import com.example.taskmanager.screens.Priority

@Preview(showBackground = true)
@Composable
fun logInScreenPreview() {
    FinishedScreen(tasks = listOf(Tasks(1, "Task 1", "Description 1", false, Priority.HIGH, "12/12/2023")))
}
