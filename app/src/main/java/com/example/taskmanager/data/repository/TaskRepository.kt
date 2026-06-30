package com.example.taskmanager.data.repository

import com.example.taskmanager.classes.Tasks
import com.example.taskmanager.data.local.dao.TaskDao
import com.example.taskmanager.data.local.entity.toEntity
import com.example.taskmanager.data.local.entity.toTasks
import com.example.taskmanager.data.preferences.UserPreferencesRepository
import com.example.taskmanager.data.remote.CreateProjectRequest
import com.example.taskmanager.data.remote.ProjectResponse
import com.example.taskmanager.data.remote.ProjectType
import com.example.taskmanager.data.remote.TaskManagerApi
import com.example.taskmanager.data.remote.UpdateProjectRequest
import com.example.taskmanager.data.remote.WorkStatus
import com.example.taskmanager.data.remote.toApiDateOrNull
import com.example.taskmanager.data.remote.toDisplayDate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskRepository(
    private val taskDao: TaskDao,
    private val api: TaskManagerApi,
    private val userPreferencesRepository: UserPreferencesRepository
) {

    val allTasks: Flow<List<Tasks>> = taskDao.getAllTasks().map { list -> list.map { it.toTasks() } }

    suspend fun syncFromBackend() {
        if (!hasBackendSession()) return

        val remoteTasks = api.getProjects()
            .filter { it.type == ProjectType.INDIVIDUAL }
            .map { it.toTask() }

        taskDao.deleteAll()
        taskDao.insertAll(remoteTasks.map { it.toEntity() })
    }

    suspend fun addTask(task: Tasks) {
        val remoteTask = createRemoteTask(task)
        taskDao.insert((remoteTask ?: task).toEntity())
    }

    suspend fun updateTask(task: Tasks) {
        val remoteTask = updateRemoteTask(task)
        taskDao.update((remoteTask ?: task).toEntity())
    }

    suspend fun deleteTask(task: Tasks) {
        // The current backend has no DELETE endpoint for projects, so deletion remains local.
        taskDao.delete(task.toEntity())
    }

    suspend fun toggleCompleted(task: Tasks) {
        val remoteTask = toggleRemoteTask(task)
        taskDao.update((remoteTask ?: task.copy(completed = !task.completed)).toEntity())
    }

    private suspend fun createRemoteTask(task: Tasks): Tasks? {
        val ownerId = userPreferencesRepository.currentUserId.first() ?: return null
        val dueDate = task.dueDate.toApiDateOrNull() ?: return null

        return runCatching {
            api.createProject(
                CreateProjectRequest(
                    name = task.title,
                    description = task.description,
                    type = ProjectType.INDIVIDUAL,
                    dueDate = dueDate,
                    ownerId = ownerId
                )
            ).toTask(priority = task.priority)
        }.getOrNull()
    }

    private suspend fun updateRemoteTask(task: Tasks): Tasks? {
        val remoteId = task.remoteId ?: return null
        val dueDate = task.dueDate.toApiDateOrNull() ?: return null

        return runCatching {
            api.updateProject(
                id = remoteId,
                request = UpdateProjectRequest(
                    name = task.title,
                    description = task.description,
                    type = ProjectType.INDIVIDUAL,
                    status = if (task.completed) WorkStatus.FINISHED else WorkStatus.PENDING,
                    dueDate = dueDate
                )
            ).toTask(localId = task.id, priority = task.priority)
        }.getOrNull()
    }

    private suspend fun toggleRemoteTask(task: Tasks): Tasks? {
        val remoteId = task.remoteId ?: return null
        val dueDate = task.dueDate.toApiDateOrNull() ?: return null

        return runCatching {
            val response = if (!task.completed) {
                api.finishProject(remoteId)
            } else {
                api.updateProject(
                    id = remoteId,
                    request = UpdateProjectRequest(
                        name = task.title,
                        description = task.description,
                        type = ProjectType.INDIVIDUAL,
                        status = WorkStatus.PENDING,
                        dueDate = dueDate
                    )
                )
            }
            response.toTask(localId = task.id, priority = task.priority)
        }.getOrNull()
    }

    private suspend fun hasBackendSession(): Boolean =
        !userPreferencesRepository.accessToken.first().isNullOrBlank()

    private fun ProjectResponse.toTask(
        localId: Long = 0,
        priority: com.example.taskmanager.classes.Priority = com.example.taskmanager.classes.Priority.MEDIUM
    ): Tasks = Tasks(
        id = localId,
        title = name,
        description = description.orEmpty(),
        completed = status == WorkStatus.FINISHED,
        priority = priority,
        dueDate = dueDate.toDisplayDate(),
        remoteId = id
    )
}
