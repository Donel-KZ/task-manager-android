package com.example.taskmanager.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.taskmanager.data.preferences.ThemeMode
import com.example.taskmanager.data.preferences.UserPreferencesRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    userPreferencesRepository: UserPreferencesRepository,
    onLogout: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val currentMode by userPreferencesRepository.themeMode.collectAsState(initial = ThemeMode.SYSTEM)
    val currentUsername by userPreferencesRepository.currentUsername.collectAsState(initial = null)
    val currentDisplayName by userPreferencesRepository.currentDisplayName.collectAsState(initial = null)

    var showLogoutConfirm by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Account section shows whoever actually signed in — no placeholder identity.
            if (currentDisplayName != null) {
                Text(text = "Account", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = currentDisplayName ?: "", style = MaterialTheme.typography.bodyLarge)
                currentUsername?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            Text(text = "Appearance", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            ThemeOptionRow(
                label = "Light",
                selected = currentMode == ThemeMode.LIGHT,
                onClick = { scope.launch { userPreferencesRepository.setThemeMode(ThemeMode.LIGHT) } }
            )
            ThemeOptionRow(
                label = "Dark",
                selected = currentMode == ThemeMode.DARK,
                onClick = { scope.launch { userPreferencesRepository.setThemeMode(ThemeMode.DARK) } }
            )
            ThemeOptionRow(
                label = "Use System Setting",
                selected = currentMode == ThemeMode.SYSTEM,
                onClick = { scope.launch { userPreferencesRepository.setThemeMode(ThemeMode.SYSTEM) } }
            )

            Spacer(modifier = Modifier.height(32.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = { showLogoutConfirm = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Log Out")
            }
        }
    }

    if (showLogoutConfirm) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirm = false },
            title = { Text("Log Out?") },
            text = { Text("You'll need to log in again to access your tasks and projects.") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutConfirm = false
                    onLogout()
                }) { Text("Log Out") }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirm = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun ThemeOptionRow(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
    }
}
