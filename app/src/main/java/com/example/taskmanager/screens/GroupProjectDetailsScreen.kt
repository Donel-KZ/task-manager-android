package com.example.taskmanager.screens


import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.taskmanager.classes.*
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupProjectDetailScreen(
    project: GroupProject,
    currentUsername: String,           // logged-in user's username
    onBack: () -> Unit,
    onProjectUpdate: (GroupProject) -> Unit  // bubble changes up to ViewModel/state
) {
    var localProject by remember { mutableStateOf(project) }
    val isOwner = localProject.members.any {
        it.username == currentUsername && it.role == Role.OWNER
    }

    // Tab state
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Deliverables", "Members")

    // Dialog visibility
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
                    // Owner-only: add member button
                    if (isOwner) {
                        IconButton(onClick = { showAddMemberDialog = true }) {
                            Icon(Icons.Default.PersonAdd, contentDescription = "Add Member")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            // Only show FAB on Deliverables tab
            if (selectedTab == 0) {
                FloatingActionButton(onClick = { showCreateDeliverableDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Create Deliverable")
                }
            }
        }
    ) { padding ->

        Column(modifier = Modifier.padding(padding)) {

            // Past due banner or Due Date info
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

            // Tabs
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
                0 -> DeliverablesTab(
                    project = localProject,
                    onProjectUpdate = ::update
                )
                1 -> MembersTab(
                    project = localProject,
                    isOwner = isOwner,
                    onProjectUpdate = ::update
                )
            }
        }
    }

    // Dialogs
    if (showAddMemberDialog) {
        AddMemberDialog(
            onDismiss = { showAddMemberDialog = false },
            onConfirm = { username ->
                // In real app this would call API to look up user by username
                val newMember = Member(
                    id = UUID.randomUUID().toString(),
                    name = username,      // real app would return full name from API
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
