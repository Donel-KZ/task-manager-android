package com.example.taskmanager.data.repository

import com.example.taskmanager.data.preferences.UserPreferencesRepository
import com.example.taskmanager.data.remote.ApiErrorResponse
import com.example.taskmanager.data.remote.ForgotPasswordRequest
import com.example.taskmanager.data.remote.LoginRequest
import com.example.taskmanager.data.remote.RegisterRequest
import com.example.taskmanager.data.remote.ResetPasswordRequest
import com.example.taskmanager.data.remote.TaskManagerApi
import java.io.IOException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import retrofit2.HttpException

class AuthRepository(
    private val api: TaskManagerApi,
    private val userPreferencesRepository: UserPreferencesRepository
) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun login(email: String, password: String) {
        val response = callAuthApi { api.login(LoginRequest(email = email.trim(), password = password)) }
        userPreferencesRepository.setSession(
            username = response.user.email,
            displayName = response.user.displayName,
            accessToken = response.accessToken,
            userId = response.user.id
        )
    }

    suspend fun register(displayName: String, email: String, password: String) {
        val response = callAuthApi {
            api.register(
                RegisterRequest(
                    displayName = displayName.trim(),
                    email = email.trim(),
                    password = password
                )
            )
        }
        userPreferencesRepository.setSession(
            username = response.user.email,
            displayName = response.user.displayName,
            accessToken = response.accessToken,
            userId = response.user.id
        )
    }

    suspend fun forgotPassword(email: String): String {
        return callAuthApi { api.forgotPassword(ForgotPasswordRequest(email.trim())) }.message
    }

    suspend fun resetPassword(token: String, password: String): String {
        return callAuthApi { api.resetPassword(ResetPasswordRequest(token.trim(), password)) }.message
    }

    private suspend fun <T> callAuthApi(block: suspend () -> T): T =
        try {
            block()
        } catch (exception: HttpException) {
            throw IllegalStateException(exception.toUserMessage(), exception)
        } catch (exception: IOException) {
            throw IllegalStateException("Cannot reach the server. Check your connection and try again.", exception)
        }

    private fun HttpException.toUserMessage(): String {
        val body = response()?.errorBody()?.string().orEmpty()
        val apiError = runCatching {
            json.decodeFromString<ApiErrorResponse>(body)
        }.getOrNull()
        return apiError?.errors?.values?.firstOrNull()
            ?: apiError?.message?.takeIf { it.isNotBlank() }
            ?: "Request failed. Please try again."
    }
}
