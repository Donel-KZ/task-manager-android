package com.example.taskmanager.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.taskmanager.classes.*

// Room works best with flat, normalized tables. GroupProject in the UI layer is a nested
// object graph (project -> members, deliverables -> taskItems, files), so we store the
// nested lists as JSON strings in a single row rather than building out 5 separate tables
// with foreign keys + junction tables. This keeps Room setup simple while still giving you
// full local persistence. If your project grows significantly, splitting into proper
// relational tables (ProjectEntity, MemberEntity, DeliverableEntity, etc. with @Relation)
// would be the next step — happy to do that refactor if you want it.

@Entity(tableName = "group_projects")
data class GroupProjectEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val status: Status,
    val pastDue: Boolean,
    val dueDate: String,
    val membersJson: String,       // serialized List<Member>
    val deliverablesJson: String,  // serialized List<Deliverable>
    val remoteId: Long? = null
)
