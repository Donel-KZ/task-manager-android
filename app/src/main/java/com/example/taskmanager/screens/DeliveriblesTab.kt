package com.example.taskmanager.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.taskmanager.classes.*
import java.util.UUID

@Composable
fun DeliverablesTab(
    project: GroupProject,
    onProjectUpdate: (GroupProject) -> Unit
) {
    if (project.deliverables.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No deliverables yet. Tap + to create one.")
        }
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(project.deliverables) { deliverable ->
            DeliverableCard(
                deliverable = deliverable,
                onDeliverableUpdate = { updated ->
                    onProjectUpdate(
                        project.copy(
                            deliverables = project.deliverables.map {
                                if (it.id == updated.id) updated else it
                            }
                        )
                    )
                }
            )
        }
    }
}

@Composable
fun DeliverableCard(
    deliverable: Deliverable,
    onDeliverableUpdate: (Deliverable) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var showAddTaskDialog by remember { mutableStateOf(false) }

    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val fileName = uri.lastPathSegment ?: "file_${System.currentTimeMillis()}"
            val newFile = AttachedFile(
                id = UUID.randomUUID().toString(),
                name = fileName,
                uri = uri.toString()
            )
            onDeliverableUpdate(deliverable.copy(files = deliverable.files + newFile))
        }
    }

    val finishedTasks = deliverable.taskItems.count { it.status == Status.FINISHED }
    val totalTasks = deliverable.taskItems.size

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = deliverable.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Due: ${deliverable.dueDate}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (deliverable.description.isNotBlank()) {
                        Text(
                            text = deliverable.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (deliverable.pastDue) {
                    SuggestionChip(
                        onClick = {},
                        label = { Text("Past Due") },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            labelColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }

                IconButton(
                    onClick = {
                        onDeliverableUpdate(
                            deliverable.copy(
                                status = if (deliverable.status == Status.FINISHED)
                                    Status.PENDING else Status.FINISHED
                            )
                        )
                    }
                ) {
                    Icon(
                        imageVector = if (deliverable.status == Status.FINISHED)
                            Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = "Toggle status",
                        tint = if (deliverable.status == Status.FINISHED)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "Collapse" else "Expand"
                    )
                }
            }

            if (totalTasks > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tasks: $finishedTasks / $totalTasks",
                    style = MaterialTheme.typography.labelSmall
                )
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { finishedTasks.toFloat() / totalTasks },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Tasks", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    TextButton(onClick = { showAddTaskDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Task")
                    }
                }

                if (deliverable.taskItems.isEmpty()) {
                    Text("No tasks yet.", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(vertical = 4.dp))
                } else {
                    deliverable.taskItems.forEach { task ->
                        TaskItemRow(
                            task = task,
                            onToggle = {
                                onDeliverableUpdate(
                                    deliverable.copy(
                                        taskItems = deliverable.taskItems.map {
                                            if (it.id == task.id)
                                                it.copy(
                                                    status = if (it.status == Status.FINISHED)
                                                        Status.PENDING else Status.FINISHED
                                                )
                                            else it
                                        }
                                    )
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Files", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    TextButton(onClick = { filePicker.launch("*/*") }) {
                        Icon(Icons.Default.AttachFile, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Attach File")
                    }
                }

                if (deliverable.files.isEmpty()) {
                    Text("No files attached.", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(vertical = 4.dp))
                } else {
                    deliverable.files.forEach { file ->
                        FileRow(
                            file = file,
                            onRemove = {
                                onDeliverableUpdate(
                                    deliverable.copy(files = deliverable.files.filter { it.id != file.id })
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    if (showAddTaskDialog) {
        AddTaskDialog(
            onDismiss = { showAddTaskDialog = false },
            onConfirm = { title ->
                val newTask = TaskItem(
                    id = UUID.randomUUID().toString(),
                    title = title,
                    status = Status.PENDING,
                    pastDue = false
                )
                onDeliverableUpdate(deliverable.copy(taskItems = deliverable.taskItems + newTask))
                showAddTaskDialog = false
            }
        )
    }
}

@Composable
fun TaskItemRow(task: TaskItem, onToggle: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = task.status == Status.FINISHED, onCheckedChange = { onToggle() })
        Text(text = task.title, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        if (task.pastDue) {
            Text(text = "Overdue", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun FileRow(file: AttachedFile, onRemove: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.InsertDriveFile,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = file.name, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
        IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Remove file", modifier = Modifier.size(16.dp))
        }
    }
}
