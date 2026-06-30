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
fun FinishedScreen(
    tasks: List<Tasks>,
    navController: NavController,
    userProfilePicUri: String?,
    onUpdateProfilePic: (Uri) -> Unit
) {
    val completedTasks = tasks.filter { it.completed }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var searching by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> uri?.let { onUpdateProfilePic(it) } }

    val displayedTasks = if (searching && searchText.isNotBlank()) {
        completedTasks.filter { it.title.contains(searchText, ignoreCase = true) }
    } else {
        completedTasks
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
                NavigationDrawerItem(label = { Text("Home") }, selected = false,
                    onClick = { scope.launch { drawerState.close() }; navController.navigate("home") })
                NavigationDrawerItem(label = { Text("Finished") }, selected = true,
                    onClick = { scope.launch { drawerState.close() } })
                NavigationDrawerItem(label = { Text("Pending") }, selected = false,
                    onClick = { scope.launch { drawerState.close() }; navController.navigate("pending") })
                NavigationDrawerItem(label = { Text("Group Project") }, selected = false,
                    onClick = { scope.launch { drawerState.close() }; navController.navigate("group_project") })
                NavigationDrawerItem(label = { Text("Overdue") }, selected = false,
                    onClick = { scope.launch { drawerState.close() }; navController.navigate("overdue") })
                NavigationDrawerItem(label = { Text("Settings") }, selected = false,
                    onClick = { scope.launch { drawerState.close() }; navController.navigate("settings") })
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
                            Text("Finished")
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
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
            }
        ) { padding ->
            if (displayedTasks.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(if (searching) "No matching tasks." else "No completed tasks.")
                }
            } else {
                LazyColumn(modifier = Modifier.padding(padding)) {
                    items(displayedTasks, key = { it.id }) { task ->
                        TaskCard(task)
                    }
                }
            }
        }
    }
}
