package com.example.taskmanager.data

import android.content.Context
import com.example.taskmanager.data.local.TaskManagerDatabase
import com.example.taskmanager.data.preferences.UserPreferencesRepository
import com.example.taskmanager.data.remote.NetworkModule
import com.example.taskmanager.data.repository.AuthRepository
import com.example.taskmanager.data.repository.GroupProjectRepository
import com.example.taskmanager.data.repository.TaskRepository

// Lightweight manual DI container — avoids pulling in Hilt/Koin for a project this size.
// Instantiate once in Application/Activity and pass down via composition.
class AppContainer(context: Context) {

    private val database = TaskManagerDatabase.getInstance(context)
    private val preferencesRepository = UserPreferencesRepository(context)
    private val api = NetworkModule.createApi(preferencesRepository)

    val taskRepository: TaskRepository by lazy {
        TaskRepository(database.taskDao(), api, preferencesRepository)
    }

    val groupProjectRepository: GroupProjectRepository by lazy {
        GroupProjectRepository(database.groupProjectDao(), api, preferencesRepository)
    }

    val authRepository: AuthRepository by lazy {
        AuthRepository(api, preferencesRepository)
    }

    val userPreferencesRepository: UserPreferencesRepository by lazy {
        preferencesRepository
    }
}
