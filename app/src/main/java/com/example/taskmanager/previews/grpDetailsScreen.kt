package com.example.taskmanager.previews

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.taskmanager.classes.*
import com.example.taskmanager.screens.GroupProjectDetailScreen

@Preview(showBackground = true)
@Composable
fun GroupProjectDetailPreview() {
    val dummyProject = GroupProject(
        id = "1",
        title = "Android App Development",
        status = Status.PENDING,
        pastDue = false,
        dueDate = "20/12/2024",
        members = listOf(
            Member("1", "John Doe", "johndoe", Role.OWNER),
            Member("2", "Jane Smith", "janesmith", Role.MEMBER)
        ),
        deliverables = listOf(
            Deliverable(
                id = "d1",
                title = "UI Implementation",
                description = "Build the main screens using Jetpack Compose",
                status = Status.PENDING,
                pastDue = false,
                dueDate = "10/12/2024"
            ),
            Deliverable(
                id = "d2",
                title = "API Integration",
                description = "Connect to the backend services",
                status = Status.PENDING,
                pastDue = true,
                dueDate = "01/12/2024"
            )
        )
    )

    GroupProjectDetailScreen(
        project = dummyProject,
        currentUsername = "johndoe",
        onBack = { /* Handle back button click */ },
        onProjectUpdate = { /* Handle project updates */ }
    )
}
