package com.example.taskmanager.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Top-level extension property — creates a single DataStore instance tied to the app context.
// This replaces SharedPreferences; DataStore is the modern, coroutine/Flow-based equivalent.
private val Context.dataStore by preferencesDataStore(name = "user_preferences")

enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

class UserPreferencesRepository(private val context: Context) {

    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val PROFILE_PIC_URI = stringPreferencesKey("profile_pic_uri")
        val CURRENT_USERNAME = stringPreferencesKey("current_username")
        val DISPLAY_NAME = stringPreferencesKey("display_name")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val USER_ID = longPreferencesKey("user_id")
    }

    // Emits the saved theme mode, defaulting to SYSTEM if never set.
    val themeMode: Flow<ThemeMode> = context.dataStore.data.map { prefs ->
        when (prefs[Keys.THEME_MODE]) {
            "LIGHT" -> ThemeMode.LIGHT
            "DARK" -> ThemeMode.DARK
            else -> ThemeMode.SYSTEM
        }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { prefs ->
            prefs[Keys.THEME_MODE] = mode.name
        }
    }

    val profilePicUri: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[Keys.PROFILE_PIC_URI]
    }

    suspend fun setProfilePicUri(uri: String?) {
        context.dataStore.edit { prefs ->
            if (uri != null) prefs[Keys.PROFILE_PIC_URI] = uri
            else prefs.remove(Keys.PROFILE_PIC_URI)
        }
    }

    val currentUsername: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[Keys.CURRENT_USERNAME]
    }

    val currentDisplayName: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[Keys.DISPLAY_NAME]
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.IS_LOGGED_IN] ?: false
    }

    val accessToken: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[Keys.ACCESS_TOKEN]
    }

    val currentUserId: Flow<Long?> = context.dataStore.data.map { prefs ->
        prefs[Keys.USER_ID]
    }

    suspend fun setSession(
        username: String,
        displayName: String,
        accessToken: String? = null,
        userId: Long? = null
    ) {
        context.dataStore.edit { prefs ->
            prefs[Keys.CURRENT_USERNAME] = username
            prefs[Keys.DISPLAY_NAME] = displayName
            prefs[Keys.IS_LOGGED_IN] = true
            if (accessToken != null) prefs[Keys.ACCESS_TOKEN] = accessToken
            else prefs.remove(Keys.ACCESS_TOKEN)
            if (userId != null) prefs[Keys.USER_ID] = userId
            else prefs.remove(Keys.USER_ID)
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs.remove(Keys.CURRENT_USERNAME)
            prefs.remove(Keys.DISPLAY_NAME)
            prefs.remove(Keys.ACCESS_TOKEN)
            prefs.remove(Keys.USER_ID)
            prefs[Keys.IS_LOGGED_IN] = false
        }
    }

    suspend fun setCurrentUsername(username: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.CURRENT_USERNAME] = username
        }
    }
}
