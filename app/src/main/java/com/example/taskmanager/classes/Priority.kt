package com.example.taskmanager.classes

import androidx.compose.ui.graphics.Color

enum class Priority(val color: Color) {
    LOW(Color(0xFF4CAF50)),
    MEDIUM(Color(0xFFFFC107)),
    HIGH(Color(0xFFF44336))
}