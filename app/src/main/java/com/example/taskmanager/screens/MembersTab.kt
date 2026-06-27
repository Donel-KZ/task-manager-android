package com.example.taskmanager.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.taskmanager.classes.GroupProject
import com.example.taskmanager.classes.Member
import com.example.taskmanager.classes.Role

@Composable
fun MembersTab(
    project: GroupProject,
    isOwner: Boolean,
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
                onRemove = {
                    // Owner cannot remove themselves
                    if (member.role != Role.OWNER) {
                        onProjectUpdate(
                            project.copy(members = project.members.filter { it.id != member.id })
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun MemberRow(
    member: Member,
    isOwner: Boolean,
    onRemove: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
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
            AssistChip(
                onClick = {},
                label = { Text(member.role.name) }
            )
            // Owner can remove members (but not themselves)
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