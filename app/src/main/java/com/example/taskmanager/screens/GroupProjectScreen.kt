package com.example.taskmanager.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.taskmanager.classes.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupProjectScreen(
    projects: SnapshotStateList<GroupProject>,
    navController: NavController,
    currentUsername: String = "donel_dev",
    userProfilePicUri: String?,
    onUpdateProfilePic: (Uri) -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var searching by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    var showCreateDialog by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onUpdateProfilePic(it) }
    }

    val displayedProjects = if (searching && searchText.isNotBlank()) {
        projects.filter { it.title.contains(searchText, ignoreCase = true) }
    } else {
        projects
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(24.dp))

                Box(modifier = Modifier.padding(start = 16.dp)) {
                    if (userProfilePicUri != null) {
                        AsyncImage(
                            model = userProfilePicUri,
                            contentDescription = "Profile picture",
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .clickable { photoPickerLauncher.launch("image/*") },
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Add profile picture",
                            modifier = Modifier
                                .size(80.dp)
                                .clickable { photoPickerLauncher.launch("image/*") },
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                NavigationDrawerItem(
                    label = { Text("Home") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate("home")
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Finished") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate("finished")
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Pending") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate("pending")
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Group Project") },
                    selected = true,
                    onClick = {
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Overdue") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate("overdue")
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        if (searching) {
                            OutlinedTextField(
                                value = searchText,
                                onValueChange = { searchText = it },
                                placeholder = { Text("Search Projects") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            Text("Group Projects")
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            searching = !searching
                            if (!searching) searchText = ""
                        }) {
                            Icon(
                                imageVector = if (searching) Icons.Default.Close else Icons.Default.Search,
                                contentDescription = if (searching) "Close search" else "Search"
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { showCreateDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Create Project")
                }
            }
        ) { padding ->
            if (displayedProjects.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(if (searching) "No matching projects." else "No group projects.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.padding(padding)
                ) {
                    items(displayedProjects) { project ->
                        GroupProjectCard(
                            project = project,
                            onClick = {
                                navController.navigate("project_details/${project.id}")
                            }
                        )
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateProjectDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { title, dueDate ->
                val newProject = GroupProject(
                    id = java.util.UUID.randomUUID().toString(),
                    title = title,
                    status = Status.PENDING,
                    pastDue = false,
                    dueDate = dueDate,
                    members = listOf(
                        Member(
                            id = java.util.UUID.randomUUID().toString(),
                            name = "Project Creator",
                            username = currentUsername,
                            role = Role.OWNER,
                            profilePictureUri = userProfilePicUri
                        )
                    ),
                    deliverables = emptyList()
                )
                projects.add(newProject)
                showCreateDialog = false
                // Auto-navigate to details so the owner can start adding members/deliverables
                navController.navigate("project_details/${newProject.id}")
            }
        )
    }
}
