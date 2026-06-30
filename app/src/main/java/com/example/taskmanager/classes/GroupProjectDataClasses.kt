package com.example.taskmanager.classes

import kotlinx.serialization.Serializable


@Serializable
enum class Role { OWNER, MEMBER }

@Serializable
enum class Status { PENDING, FINISHED, OVERDUE }

@Serializable
data class AttachedFile(
    val id: String,
    val name: String,
    val uri: String
)

@Serializable
data class Member(
    val id: String,
    val name: String,
    val username: String,   // <-- needed for add-by-username
    val role: Role,
    val profilePictureUri: String? = null, // Add profile picture URI
    val remoteMembershipId: Long? = null,
    val remoteUserId: Long? = null
)

@Serializable
data class TaskItem(
    val id: String,
    val title: String,
    val status: Status,
    val pastDue: Boolean,
    val dueDate: String = "",
    val remoteId: Long? = null
)

@Serializable
data class Deliverable(
    val id: String,
    val title: String,
    val description: String = "",
    val status: Status,
    val pastDue: Boolean,
    val dueDate: String,
    val taskItems: List<TaskItem> = emptyList(),
    val files: List<AttachedFile> = emptyList(),
    val remoteId: Long? = null
)

@Serializable
data class GroupProject(
    val id: String,
    val title: String,
    val status: Status,
    val pastDue: Boolean,
    val dueDate: String,
    val members: List<Member> = emptyList(),
    val deliverables: List<Deliverable> = emptyList(),
    val remoteId: Long? = null
)
