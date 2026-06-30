package com.example.taskmanager.data

import android.content.Context
import com.example.taskmanager.data.local.TaskManagerDatabase
import com.example.taskmanager.data.preferences.UserPreferencesRepository
import com.example.taskmanager.data.repository.GroupProjectRepository
import com.example.taskmanager.data.repository.TaskRepository

// Lightweight manual DI container — avoids pulling in Hilt/Koin for a project this size.
// Instantiate once in Application/Activity and pass down via composition.
class AppContainer(context: Context) {

    private val database = TaskManagerDatabase.getInstance(context)

    val taskRepository: TaskRepository by lazy {
        TaskRepository(database.taskDao())
    }

    val groupProjectRepository: GroupProjectRepository by lazy {
        GroupProjectRepository(database.groupProjectDao())
    }

    val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(context)
    }
}
