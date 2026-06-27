package com.example.taskmanager.classes



enum class Role { OWNER, MEMBER }
enum class Status { PENDING, FINISHED, OVERDUE }

data class AttachedFile(
    val id: String,
    val name: String,
    val uri: String
)

data class Member(
    val id: String,
    val name: String,
    val username: String,   // <-- needed for add-by-username
    val role: Role
)

data class TaskItem(
    val id: String,
    val title: String,
    val status: Status,
    val pastDue: Boolean
)

data class Deliverable(
    val id: String,
    val title: String,
    val description: String = "",
    val status: Status,
    val pastDue: Boolean,
    val dueDate: String,
    val taskItems: List<TaskItem> = emptyList(),
    val files: List<AttachedFile> = emptyList()
)

data class GroupProject(
    val id: String,
    val title: String,
    val status: Status,
    val pastDue: Boolean,
    val dueDate: String,
    val members: List<Member> = emptyList(),
    val deliverables: List<Deliverable> = emptyList()
)