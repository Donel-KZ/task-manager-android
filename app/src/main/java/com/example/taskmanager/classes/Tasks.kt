package com.example.taskmanager.classes

data class Tasks(
    var id: Long,
    var title: String,
    var description: String,
    var completed: Boolean,
    var priority: Priority,
    var dueDate: String
)