package com.example.taskmanager

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.taskmanager.data.AppContainer
import com.example.taskmanager.data.preferences.ThemeMode
import com.example.taskmanager.ui.theme.TaskManagerTheme

class MainActivity : ComponentActivity() {

    // Manual DI container — holds Room DB + DataStore repositories for the app's lifetime.
    private lateinit var appContainer: AppContainer

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appContainer = AppContainer(applicationContext)

        setContent {
            // Reads the persisted theme preference from DataStore as a Compose State.
            // Defaults to ThemeMode.SYSTEM until the first value arrives.
            val themeMode by appContainer.userPreferencesRepository.themeMode
                .collectAsState(initial = ThemeMode.SYSTEM)

            val useDarkTheme = when (themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> androidx.compose.foundation.isSystemInDarkTheme()
            }

            TaskManagerTheme(darkTheme = useDarkTheme) {
                Surface(color = MaterialTheme.colorScheme.background) {
                    AppNavigation(appContainer = appContainer)
                }
            }
        }
    }
}
