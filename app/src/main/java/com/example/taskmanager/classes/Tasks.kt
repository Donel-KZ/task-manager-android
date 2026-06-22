package com.example.taskmanager.classes

import com.example.taskmanager.screens.Priority

data class Tasks(
    var id: Long,
    var title: String,
    var description: String,
    var completed: Boolean,
    var priority: Priority,
    var dueDate: String
)