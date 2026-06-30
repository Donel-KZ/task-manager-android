package com.example.taskmanager.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.taskmanager.classes.*
import java.util.UUID

// NOTE: The duplicate GroupProjectDetailScreen.kt file has been removed.
// This is the single authoritative version.
// BUG FIX: currentUsername is now passed down to MembersTab instead of relying
// on the hardcoded default in MembersTab.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupProjectDetailScreen(
    project: GroupProject,
    currentUsername: String,
    onBack: () -> Unit,
    onProjectUpdate: (GroupProject) -> Unit
) {
    var localProject by remember { mutableStateOf(project) }
    val isOwner = localProject.members.any {
        it.username == currentUsername && it.role == Role.OWNER
    }

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Deliverables", "Members")

    var showAddMemberDialog by remember { mutableStateOf(false) }
    var showCreateDeliverableDialog by remember { mutableStateOf(false) }

    fun update(updated: GroupProject) {
        localProject = updated
        onProjectUpdate(updated)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(localProject.title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (isOwner) {
                        IconButton(onClick = { showAddMemberDialog = true }) {
                            Icon(Icons.Default.PersonAdd, contentDescription = "Add Member")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(onClick = { showCreateDeliverableDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Create Deliverable")
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (localProject.pastDue) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "⚠ This project is past due (Due: ${localProject.dueDate})",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            } else {
                Text(
                    text = "Project Due Date: ${localProject.dueDate}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            when (selectedTab) {
                0 -> DeliverablesTab(project = localProject, onProjectUpdate = ::update)
                // BUG FIX: currentUsername now passed to MembersTab
                1 -> MembersTab(
                    project = localProject,
                    isOwner = isOwner,
                    currentUsername = currentUsername,
                    onProjectUpdate = ::update
                )
            }
        }
    }

    if (showAddMemberDialog) {
        AddMemberDialog(
            onDismiss = { showAddMemberDialog = false },
            onConfirm = { username ->
                val newMember = Member(
                    id = UUID.randomUUID().toString(),
                    name = username,
                    username = username,
                    role = Role.MEMBER
                )
                update(localProject.copy(members = localProject.members + newMember))
                showAddMemberDialog = false
            }
        )
    }

    if (showCreateDeliverableDialog) {
        CreateDeliverableDialog(
            projectDueDate = localProject.dueDate,
            onDismiss = { showCreateDeliverableDialog = false },
            onConfirm = { title, description, dueDate ->
                val newDeliverable = Deliverable(
                    id = UUID.randomUUID().toString(),
                    title = title,
                    description = description,
                    status = Status.PENDING,
                    pastDue = false,
                    dueDate = dueDate
                )
                update(localProject.copy(deliverables = localProject.deliverables + newDeliverable))
                showCreateDeliverableDialog = false
            }
        )
    }
}
