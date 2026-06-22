package com.example.taskmanager.previews

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.taskmanager.screens.SignUpScreen


@Preview(showBackground = true)
@Composable
fun SignUpPreview() {

    SignUpScreen(
        navController = rememberNavController(),
        onSignUpClick = { name, email, password ->

        }
    )
}