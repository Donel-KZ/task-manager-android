package com.example.taskmanager.previews

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.taskmanager.screens.AddTaskContent



@Preview(showBackground = true)
@Composable
fun CalendarScreenPreview() {


        AddTaskContent(

            onCancel = { },

            onSave = { task ->
                // Preview only
            }
        )


}