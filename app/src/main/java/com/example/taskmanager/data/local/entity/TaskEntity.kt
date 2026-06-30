package com.example.taskmanager.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.taskmanager.classes.Priority
import com.example.taskmanager.classes.Tasks

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val completed: Boolean,
    val priority: Priority,
    val dueDate: String
)

// Mappers between the Room entity and the existing UI model (Tasks),
// so screens don't need to change how they consume task data.
fun TaskEntity.toTasks(): Tasks = Tasks(
    id = id,
    title = title,
    description = description,
    completed = completed,
    priority = priority,
    dueDate = dueDate
)

fun Tasks.toEntity(): TaskEntity = TaskEntity(
    id = id,
    title = title,
    description = description,
    completed = completed,
    priority = priority,
    dueDate = dueDate
)
