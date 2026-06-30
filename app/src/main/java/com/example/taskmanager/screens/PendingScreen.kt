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
import com.example.taskmanager.classes.Tasks
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PendingScreens(
    tasks: SnapshotStateList<Tasks>,
    navController: NavController,
    userProfilePicUri: String?,
    onUpdateProfilePic: (Uri) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    var searching by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> uri?.let { onUpdateProfilePic(it) } }

    val filteredTasks = tasks.filter {
        !it.completed && (
                searchText.isBlank() ||
                        it.title.contains(searchText, ignoreCase = true) ||
                        it.description.contains(searchText, ignoreCase = true)
                )
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
                            modifier = Modifier.size(80.dp).clip(CircleShape)
                                .clickable { photoPickerLauncher.launch("image/*") },
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Add profile picture",
                            modifier = Modifier.size(80.dp)
                                .clickable { photoPickerLauncher.launch("image/*") },
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                NavigationDrawerItem(label = { Text("Home") }, selected = false,
                    onClick = { scope.launch { drawerState.close() }; navController.navigate("home") })
                NavigationDrawerItem(label = { Text("Finished") }, selected = false,
                    onClick = { scope.launch { drawerState.close() }; navController.navigate("finished") })
                NavigationDrawerItem(label = { Text("Pending") }, selected = true,
                    onClick = { scope.launch { drawerState.close() } })
                NavigationDrawerItem(label = { Text("Group Project") }, selected = false,
                    onClick = { scope.launch { drawerState.close() }; navController.navigate("group_project") })
                NavigationDrawerItem(label = { Text("Overdue") }, selected = false,
                    onClick = { scope.launch { drawerState.close() }; navController.navigate("overdue") })
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
                                placeholder = { Text("Search Tasks") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            Text("Pending")
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            if (searching) searchText = ""
                            searching = !searching
                        }) {
                            Icon(
                                imageVector = if (searching) Icons.Default.Close else Icons.Default.Search,
                                contentDescription = null
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { showBottomSheet = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Task")
                }
            }
        ) { padding ->
            if (filteredTasks.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(if (searching) "No matching tasks." else "No pending tasks.")
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
                    items(filteredTasks) { task ->
                        // BUG FIX: wired up toggle so pending tasks can be marked complete
                        TaskCard(
                            task = task,
                            onToggle = {
                                val index = tasks.indexOfFirst { it.id == task.id }
                                if (index != -1) {
                                    tasks[index] = tasks[index].copy(completed = !tasks[index].completed)
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(onDismissRequest = { showBottomSheet = false }) {
            AddTaskContent(
                onCancel = { showBottomSheet = false },
                onSave = { task ->
                    tasks.add(task.copy(id = tasks.size.toLong() + 1))
                    showBottomSheet = false
                }
            )
        }
    }
}
