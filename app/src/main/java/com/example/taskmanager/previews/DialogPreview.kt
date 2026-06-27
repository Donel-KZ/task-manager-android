package com.example.taskmanager.previews

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.taskmanager.screens.CreateDeliverableDialog

@Preview(showBackground = true)
@Composable
fun DialogPreview(){
    CreateDeliverableDialog(
        projectDueDate = "31/12/2024",
        onDismiss = { /* Handle dismiss */ },
        onConfirm = { title, description, dueDate ->
            // Handle confirm with provided title, description, and dueDate
        }
    )
}