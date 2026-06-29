package com.example.taskmanager.previews

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.taskmanager.classes.Tasks
import com.example.taskmanager.screens.PendingScreens

@Preview(showBackground = true)
@Composable
fun PendingProjectPreview() {
    // Providing an empty stateful list of tasks for the preview
    val tasks = remember { mutableStateListOf<Tasks>() }

    PendingScreens(
        tasks = tasks,
        navController = rememberNavController(),
        userProfilePicUri = null,
        onUpdateProfilePic = { }
    )
}
