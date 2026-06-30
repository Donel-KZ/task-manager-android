package com.example.taskmanager.data.local

import androidx.room.TypeConverter
import com.example.taskmanager.classes.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString

class Converters {

    private val json = Json { ignoreUnknownKeys = true }

    // ── Enums ──
    @TypeConverter
    fun fromPriority(value: Priority): String = value.name

    @TypeConverter
    fun toPriority(value: String): Priority = Priority.valueOf(value)

    @TypeConverter
    fun fromStatus(value: Status): String = value.name

    @TypeConverter
    fun toStatus(value: String): Status = Status.valueOf(value)

    // ── Nested object lists, stored as JSON ──
    @TypeConverter
    fun fromMemberList(value: List<Member>): String = json.encodeToString(value)

    @TypeConverter
    fun toMemberList(value: String): List<Member> =
        if (value.isBlank()) emptyList() else json.decodeFromString(value)

    @TypeConverter
    fun fromDeliverableList(value: List<Deliverable>): String = json.encodeToString(value)

    @TypeConverter
    fun toDeliverableList(value: String): List<Deliverable> =
        if (value.isBlank()) emptyList() else json.decodeFromString(value)
}
