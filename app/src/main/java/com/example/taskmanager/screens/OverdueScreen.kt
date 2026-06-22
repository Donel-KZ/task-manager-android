package com.example.taskmanager.screens

import android.os.Build
import androidx.annotation.RequiresApi
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverdueScreen(tasks: List<Tasks>) {

    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val today = LocalDate.now()

    val overdueTasks = tasks.filter { task ->
        if (task.completed || task.dueDate == null) return@filter false
        try {
            LocalDate.parse(task.dueDate, formatter).isBefore(today)
        } catch (e: DateTimeParseException) {
            false
        }
    }

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var searching by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }

    val displayedTasks = if (searching && searchText.isNotBlank()) {
        overdueTasks.filter { it.title.contains(searchText, ignoreCase = true) }
    } else {
        overdueTasks
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
                    selected = false,
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
                    selected = true, // <-- this screen is selected
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
                            Text("Overdue") // <-- different title
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
            }
        ) { padding ->
            if (displayedTasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(if (searching) "No matching tasks." else "No overdue tasks.") // <-- different empty message
                }
            } else {
                LazyColumn(
                    modifier = Modifier.padding(padding)
                ) {
                    items(displayedTasks) { task ->
                        TaskCard(task)
                    }
                }
            }
        }
    }
}