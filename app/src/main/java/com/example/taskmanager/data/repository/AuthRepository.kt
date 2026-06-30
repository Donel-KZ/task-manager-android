package com.example.taskmanager.data.repository

import com.example.taskmanager.data.preferences.UserPreferencesRepository
import com.example.taskmanager.data.remote.LoginRequest
import com.example.taskmanager.data.remote.RegisterRequest
import com.example.taskmanager.data.remote.TaskManagerApi

class AuthRepository(
    private val api: TaskManagerApi,
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend fun login(email: String, password: String) {
        val response = api.login(LoginRequest(email = email, password = password))
        userPreferencesRepository.setSession(
            username = response.user.email,
            displayName = response.user.displayName,
            accessToken = response.accessToken,
            userId = response.user.id
        )
    }

    suspend fun register(displayName: String, email: String, password: String) {
        val response = api.register(
            RegisterRequest(
                displayName = displayName,
                email = email,
                password = password
            )
        )
        userPreferencesRepository.setSession(
            username = response.user.email,
            displayName = response.user.displayName,
            accessToken = response.accessToken,
            userId = response.user.id
        )
    }
}
