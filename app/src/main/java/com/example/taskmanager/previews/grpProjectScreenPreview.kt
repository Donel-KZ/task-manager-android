package com.example.taskmanager.previews

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.taskmanager.classes.GroupProject
import com.example.taskmanager.classes.Status
import com.example.taskmanager.screens.GroupProjectScreen

@Preview(showBackground = true)
@Composable
fun GroupProjectScreenPreview() {
    val navController = rememberNavController()
    val dummyProjects = remember {
        mutableStateListOf(
            GroupProject(
                id = "1",
                title = "Android App Development",
                status = Status.PENDING,
                pastDue = false,
                dueDate = "20/12/2024"
            ),
            GroupProject(
                id = "2",
                title = "UI/UX Design Mockups",
                status = Status.FINISHED,
                pastDue = false,
                dueDate = "15/12/2024"
            ),
            GroupProject(
                id = "3",
                title = "Backend API Integration",
                status = Status.OVERDUE,
                pastDue = true,
                dueDate = "01/12/2024"
            )
        )
    }

    GroupProjectScreen(
        projects = dummyProjects,
        navController = navController,
        userProfilePicUri = null,
        onUpdateProfilePic = { }
    )
}
