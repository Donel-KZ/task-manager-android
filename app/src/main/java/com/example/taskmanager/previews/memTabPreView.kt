package com.example.taskmanager.previews

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.taskmanager.classes.GroupProject
import com.example.taskmanager.classes.Member
import com.example.taskmanager.classes.Role
import com.example.taskmanager.classes.Status
import com.example.taskmanager.screens.MembersTab

@Preview(showBackground = true)
@Composable
fun MembersTabPreview() {
    val dummyProject = GroupProject(
        id = "1",
        title = "Sample Project",
        status = Status.PENDING,
        pastDue = false,
        dueDate = "31/12/2024",
        members = listOf(
            Member("1", "John Doe", "johndoe", Role.OWNER),
            Member("2", "Jane Smith", "janesmith", Role.MEMBER)
        )
    )

    MembersTab(
        project = dummyProject,
        isOwner = true,
        onProjectUpdate = { }
    )
}
