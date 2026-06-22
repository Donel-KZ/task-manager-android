@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.taskmanager.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview

import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.taskmanager.classes.Tasks
import kotlinx.coroutines.launch




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    var showBottomSheet by remember {
        mutableStateOf(false)
    }
    var searching by remember {
        mutableStateOf(false)
    }
    var searchText by remember {
        mutableStateOf("")
    }



    val tasks = remember {
        mutableStateListOf(
            Tasks(
                id = 1,
                title = "Finish Android UI",
                description = "Create Home Screen",
                completed = false,
                priority = Priority.HIGH,
                dueDate = "12/12/2023",
            ),
            Tasks(
                id = 2,
                title = "Connect Backend",
                description = "Integrate Spring Boot API",
                completed = false,
                priority = Priority.MEDIUM,
                dueDate = "12/12/2023",
            )
        )
    }
    val filteredTasks = tasks.filter {

        it.title.contains(
            searchText,
            ignoreCase = true
        ) ||

                it.description.contains(
                    searchText,
                    ignoreCase = true
                )
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {

        items(filteredTasks) { task ->

            TaskCard(task)

        }
    }





    val drawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed
    )

    val scope = rememberCoroutineScope()

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
                    selected = true,
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
                                onValueChange = {
                                    searchText = it
                                },
                                placeholder = {
                                    Text("Search Tasks")
                                },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()

                            )


                        } else {

                            Text("Home")
                        }



                    },

                    navigationIcon = {

                        IconButton(
                            onClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu"
                            )
                        }
                    },

                    actions = {

                        IconButton(
                            onClick = {

                                if (searching) {
                                    searchText = ""
                                }

                                searching = !searching
                            }
                        ) {

                            Icon(
                                imageVector =
                                    if (searching)
                                        Icons.Default.Close
                                    else
                                        Icons.Default.Search,

                                contentDescription = null
                            )
                        }
                    }
                )

            },


            floatingActionButton = {

                FloatingActionButton(
                    onClick = {
                        showBottomSheet = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Task"
                    )
                }
            }

        ) { padding ->

            if (tasks.isEmpty()) {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "Hello user,\nWould you like to add a task?"
                    )
                }

            } else {

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {

                    items(filteredTasks) { task ->

                        TaskCard(task)

                    }
                }
            }
        }
    }
    if (showBottomSheet) {

        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            }
        ) {

            AddTaskContent(

                onCancel = {
                    showBottomSheet = false
                },

                onSave = { task ->

                    tasks.add(task.copy(id = tasks.size.toLong() + 1))

                    showBottomSheet = false
                }
            )
        }
    }
}



@Composable
fun TaskCard(task: Tasks) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = task.title,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = task.description
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (task.completed)
                    "Completed"
                else
                    "Pending"
            )
        }
    }
}