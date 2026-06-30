package com.example.taskmanager.data.repository

import com.example.taskmanager.classes.Deliverable
import com.example.taskmanager.classes.GroupProject
import com.example.taskmanager.classes.Member
import com.example.taskmanager.data.local.Converters
import com.example.taskmanager.data.local.dao.GroupProjectDao
import com.example.taskmanager.data.local.entity.GroupProjectEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GroupProjectRepository(private val dao: GroupProjectDao) {

    private val converters = Converters()

    val allProjects: Flow<List<GroupProject>> = dao.getAllProjects().map { entities ->
        entities.map { it.toGroupProject() }
    }

    suspend fun upsert(project: GroupProject) {
        dao.insert(project.toEntity())
    }

    suspend fun delete(project: GroupProject) {
        dao.delete(project.toEntity())
    }

    private fun GroupProjectEntity.toGroupProject(): GroupProject = GroupProject(
        id = id,
        title = title,
        status = status,
        pastDue = pastDue,
        dueDate = dueDate,
        members = converters.toMemberList(membersJson),
        deliverables = converters.toDeliverableList(deliverablesJson)
    )

    private fun GroupProject.toEntity(): GroupProjectEntity = GroupProjectEntity(
        id = id,
        title = title,
        status = status,
        pastDue = pastDue,
        dueDate = dueDate,
        membersJson = converters.fromMemberList(members),
        deliverablesJson = converters.fromDeliverableList(deliverables)
    )
}
