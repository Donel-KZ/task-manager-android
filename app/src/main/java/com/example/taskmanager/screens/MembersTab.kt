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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.taskmanager.classes.GroupProject
import com.example.taskmanager.classes.Member
import com.example.taskmanager.classes.Role

// BUG FIX: currentUsername no longer has a hardcoded default ("donel_dev").
// It must be passed from GroupProjectDetailScreen which gets it from AppNavigation.
// Hardcoding it meant any user other than "donel_dev" would never match isCurrentUser.
@Composable
fun MembersTab(
    project: GroupProject,
    isOwner: Boolean,
    currentUsername: String,          // no default — must be passed explicitly
    onProjectUpdate: (GroupProject) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(project.members) { member ->
            MemberRow(
                member = member,
                isOwner = isOwner,
                isCurrentUser = member.username == currentUsername,
                onRemove = {
                    if (member.role != Role.OWNER) {
                        onProjectUpdate(
                            project.copy(members = project.members.filter { it.id != member.id })
                        )
                    }
                },
                onUpdateProfilePicture = { uri ->
                    onProjectUpdate(
                        project.copy(members = project.members.map {
                            if (it.id == member.id) it.copy(profilePictureUri = uri.toString()) else it
                        })
                    )
                }
            )
        }
    }
}

@Composable
fun MemberRow(
    member: Member,
    isOwner: Boolean,
    isCurrentUser: Boolean,
    onRemove: () -> Unit,
    onUpdateProfilePicture: (Uri) -> Unit
) {
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> uri?.let { onUpdateProfilePicture(it) } }

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (member.profilePictureUri != null) {
                AsyncImage(
                    model = member.profilePictureUri,
                    contentDescription = "Profile picture",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable(enabled = isCurrentUser) { photoPickerLauncher.launch("image/*") },
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Default profile picture",
                    modifier = Modifier
                        .size(40.dp)
                        .clickable(enabled = isCurrentUser) { photoPickerLauncher.launch("image/*") },
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = member.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "@${member.username}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AssistChip(onClick = {}, label = { Text(member.role.name) })

            if (isOwner && member.role != Role.OWNER) {
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = onRemove) {
                    Icon(
                        imageVector = Icons.Default.PersonRemove,
                        contentDescription = "Remove member",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

