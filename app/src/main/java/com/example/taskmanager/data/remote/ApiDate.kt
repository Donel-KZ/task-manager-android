package com.example.taskmanager.data.remote

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val NO_DATE_SELECTED = "No Date Selected"

private fun displayDateFormatter() = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
    isLenient = false
}

private fun apiDateFormatter() = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
    isLenient = false
}

fun String.toApiDateOrNull(): String? {
    val value = trim()
    if (value.isBlank() || value == NO_DATE_SELECTED) return null

    return try {
        val parsed = displayDateFormatter().parse(value) ?: return null
        apiDateFormatter().format(parsed)
    } catch (_: ParseException) {
        try {
            apiDateFormatter().format(apiDateFormatter().parse(value) ?: return null)
        } catch (_: ParseException) {
            null
        }
    }
}

fun String.toDisplayDate(): String {
    val value = trim()
    if (value.isBlank()) return value

    return try {
        val parsed = apiDateFormatter().parse(value) ?: return value
        displayDateFormatter().format(parsed)
    } catch (_: ParseException) {
        value
    }
}

fun todayApiDate(): String = apiDateFormatter().format(Date())
