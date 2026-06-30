package com.example.taskmanager.data.remote

import com.example.taskmanager.BuildConfig
import com.example.taskmanager.data.preferences.UserPreferencesRepository
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

object NetworkModule {
    @OptIn(ExperimentalSerializationApi::class)
    fun createApi(userPreferencesRepository: UserPreferencesRepository): TaskManagerApi {
        val json = Json {
            ignoreUnknownKeys = true
            explicitNulls = false
        }

        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val token = runBlocking { userPreferencesRepository.accessToken.first() }
                val request = if (token.isNullOrBlank()) {
                    chain.request()
                } else {
                    chain.request().newBuilder()
                        .header("Authorization", "Bearer $token")
                        .build()
                }
                chain.proceed(request)
            }
            .build()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.TASK_MANAGER_API_BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(TaskManagerApi::class.java)
    }
}
