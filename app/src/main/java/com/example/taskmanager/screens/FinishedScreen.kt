package com.example.taskmanager.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.taskmanager.classes.Tasks
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinishedScreen(tasks: List<Tasks>) {

    val completedTasks = tasks.filter { it.completed }

    // FIX 1: Define drawerState here
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // FIX 2: Define search state variables
    var searching by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }

    // Filter by search text when searching is active
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

                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                NavigationDrawerItem(
                    label = { Text("Home") },
                    selected = false,
                    onClick = { }
                )
                NavigationDrawerItem(
                    label = { Text("Finished") },
                    selected = true,
                    onClick = { }
                )
                NavigationDrawerItem(
                    label = { Text("Pending") },
                    selected = false,
                    onClick = { }
                )
                NavigationDrawerItem(
                    label = { Text("Group Project") },
                    selected = false,
                    onClick = { }
                )
                NavigationDrawerItem(
                    label = { Text("Overdue") },
                    selected = false,
                    onClick = { }
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
                                placeholder = { Text("Search Tasks") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            // FIX 3: Typo fixed — "Finishe" → "Finished"
                            Text("Finished")
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
                    // FIX 4: Wire up search/close toggle in actions
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
            }
        ) { padding ->  // FIX 5: Pass padding to the content
            if (displayedTasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),  // padding applied here
                    contentAlignment = Alignment.Center
                ) {
                    Text(if (searching) "No matching tasks." else "No completed tasks.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.padding(padding)  // and here
                ) {
                    items(displayedTasks) { task ->
                        TaskCard(task)
                    }
                }
            }
        }
    }
}