package com.example.taskmanager.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ResetPasswordScreen(
    navController: NavController,
    token: String,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onResetPassword: (String, String) -> Unit
) {
    var newPassword by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var localError by rememberSaveable { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
    ) {
        Text(
            text = "Reset Password",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = if (token.isBlank()) {
                "This reset link is missing a token. Please request a new password reset link."
            } else {
                "Enter your new password below."
            },
            style = MaterialTheme.typography.bodyMedium
        )
        OutlinedTextField(
            value = newPassword,
            onValueChange = { 
                newPassword = it
                localError = null
            },
            label = { Text("New Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isLoading && token.isNotBlank()
        )
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { 
                confirmPassword = it
                localError = null
            },
            label = { Text("Confirm New Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isLoading && token.isNotBlank()
        )
        
        val displayError = localError ?: errorMessage
        if (displayError != null) {
            Text(
                text = displayError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Button(
            onClick = {
                if (newPassword != confirmPassword) {
                    localError = "Passwords do not match."
                } else if (newPassword.length < 8) {
                    localError = "Password must be at least 8 characters."
                } else {
                    onResetPassword(token, newPassword)
                }
            },
            enabled = !isLoading &&
                token.isNotBlank() &&
                newPassword.isNotBlank() &&
                confirmPassword.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isLoading) "Resetting..." else "Reset Password")
        }

        TextButton(
            onClick = { navController.navigate("login") { popUpTo("login") { inclusive = true } } },
            enabled = !isLoading
        ) {
            Text("Back to Log In")
        }
    }
}
