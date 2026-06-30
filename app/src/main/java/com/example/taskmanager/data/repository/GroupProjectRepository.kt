package com.example.taskmanager.data.repository

import com.example.taskmanager.classes.Deliverable
import com.example.taskmanager.classes.GroupProject
import com.example.taskmanager.classes.Member
import com.example.taskmanager.classes.Role
import com.example.taskmanager.classes.Status
import com.example.taskmanager.classes.TaskItem
import com.example.taskmanager.data.local.Converters
import com.example.taskmanager.data.local.dao.GroupProjectDao
import com.example.taskmanager.data.local.entity.GroupProjectEntity
import com.example.taskmanager.data.preferences.UserPreferencesRepository
import com.example.taskmanager.data.remote.AddMemberRequest
import com.example.taskmanager.data.remote.CreateDeliverableRequest
import com.example.taskmanager.data.remote.CreateProjectRequest
import com.example.taskmanager.data.remote.CreateTaskItemRequest
import com.example.taskmanager.data.remote.DeliverableResponse
import com.example.taskmanager.data.remote.ProjectMemberResponse
import com.example.taskmanager.data.remote.ProjectResponse
import com.example.taskmanager.data.remote.ProjectRole
import com.example.taskmanager.data.remote.ProjectType
import com.example.taskmanager.data.remote.TaskItemResponse
import com.example.taskmanager.data.remote.TaskManagerApi
import com.example.taskmanager.data.remote.UpdateDeliverableRequest
import com.example.taskmanager.data.remote.UpdateProjectRequest
import com.example.taskmanager.data.remote.UpdateTaskItemRequest
import com.example.taskmanager.data.remote.UserResponse
import com.example.taskmanager.data.remote.WorkStatus
import com.example.taskmanager.data.remote.toApiDateOrNull
import com.example.taskmanager.data.remote.toDisplayDate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GroupProjectRepository(
    private val dao: GroupProjectDao,
    private val api: TaskManagerApi,
    private val userPreferencesRepository: UserPreferencesRepository
) {

    private val converters = Converters()

    val allProjects: Flow<List<GroupProject>> = dao.getAllProjects().map { entities ->
        entities.map { it.toGroupProject() }
    }

    suspend fun syncFromBackend() {
        if (!hasBackendSession()) return

        val projects = api.getProjects()
            .filter { it.type == ProjectType.GROUP }
            .map { response ->
                val members = runCatching { api.getProjectMembers(response.id) }
                    .getOrDefault(emptyList())
                    .map { it.toMember() }
                val deliverables = runCatching { api.getDeliverables(response.id) }.getOrDefault(emptyList())
                    .map { deliverable ->
                        val tasks = runCatching { api.getTasks(deliverable.id) }
                            .getOrDefault(emptyList())
                            .map { it.toTaskItem() }
                        deliverable.toDeliverable(tasks)
                    }
                response.toGroupProject(members, deliverables)
            }

        dao.deleteAll()
        dao.insertAll(projects.map { it.toEntity() })
    }

    suspend fun upsert(project: GroupProject): GroupProject {
        val saved = saveRemoteProject(project) ?: project
        dao.insert(saved.toEntity())
        return saved
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
        deliverables = converters.toDeliverableList(deliverablesJson),
        remoteId = remoteId
    )

    private fun GroupProject.toEntity(): GroupProjectEntity = GroupProjectEntity(
        id = id,
        title = title,
        status = status,
        pastDue = pastDue,
        dueDate = dueDate,
        membersJson = converters.fromMemberList(members),
        deliverablesJson = converters.fromDeliverableList(deliverables),
        remoteId = remoteId
    )

    private suspend fun saveRemoteProject(project: GroupProject): GroupProject? {
        val ownerId = userPreferencesRepository.currentUserId.first() ?: return null
        val dueDate = project.dueDate.toApiDateOrNull() ?: return null

        return runCatching {
            val remoteId = project.remoteId ?: project.id.toLongOrNull()
            val response = if (remoteId == null) {
                api.createProject(
                    CreateProjectRequest(
                        name = project.title,
                        description = null,
                        type = ProjectType.GROUP,
                        dueDate = dueDate,
                        ownerId = ownerId
                    )
                )
            } else {
                val updateRequest = UpdateProjectRequest(
                    name = project.title,
                    description = null,
                    type = ProjectType.GROUP,
                    status = project.status.toWorkStatus(),
                    dueDate = dueDate
                )
                if (project.status == Status.FINISHED) {
                    api.finishProject(remoteId)
                } else {
                    api.updateProject(remoteId, updateRequest)
                }
            }

            val members = syncMembers(response.id, project.members)
            val deliverables = syncDeliverables(response.id, project.deliverables)
            response.toGroupProject(members, deliverables, localId = project.id.takeIf { remoteId != null })
        }.getOrNull()
    }

    private suspend fun syncMembers(projectId: Long, localMembers: List<Member>): List<Member> {
        val remoteMembers = runCatching { api.getProjectMembers(projectId) }.getOrDefault(emptyList())
        var users: List<UserResponse>? = null

        val membersToAdd = localMembers
            .filter { member -> remoteMembers.none { it.user.email == member.username } }
            .filter { it.role != Role.OWNER }

        for (member in membersToAdd) {
            val knownUsers = users ?: runCatching { api.getUsers() }
                .getOrDefault(emptyList())
                .also { users = it }
            val user = knownUsers.findByMember(member) ?: continue
            runCatching {
                api.addProjectMember(
                    projectId,
                    AddMemberRequest(userId = user.id, role = member.role.toProjectRole())
                )
            }
        }

        val refreshed = runCatching { api.getProjectMembers(projectId) }.getOrDefault(remoteMembers)
        val syncedMembers = refreshed.map { it.toMember() }
        val unsyncedMembers = localMembers.filter { local ->
            syncedMembers.none { it.username == local.username }
        }
        return syncedMembers + unsyncedMembers
    }

    private suspend fun syncDeliverables(projectId: Long, deliverables: List<Deliverable>): List<Deliverable> =
        deliverables.map { deliverable ->
            val dueDate = deliverable.dueDate.toApiDateOrNull() ?: return@map deliverable
            val remoteId = deliverable.remoteId ?: deliverable.id.toLongOrNull()
            val response = if (remoteId == null) {
                runCatching {
                    api.createDeliverable(
                        projectId,
                        CreateDeliverableRequest(
                            title = deliverable.title,
                            description = deliverable.description,
                            dueDate = dueDate
                        )
                    )
                }.getOrNull()
            } else {
                runCatching {
                    if (deliverable.status == Status.FINISHED) {
                        api.finishDeliverable(remoteId)
                    } else {
                        api.updateDeliverable(
                            remoteId,
                            UpdateDeliverableRequest(
                                title = deliverable.title,
                                description = deliverable.description,
                                status = deliverable.status.toWorkStatus(),
                                dueDate = dueDate
                            )
                        )
                    }
                }.getOrNull()
            } ?: return@map deliverable

            val tasks = syncTaskItems(response.id, deliverable.taskItems)
            response.toDeliverable(tasks).copy(files = deliverable.files)
        }

    private suspend fun syncTaskItems(deliverableId: Long, tasks: List<TaskItem>): List<TaskItem> =
        tasks.map { task ->
            val remoteId = task.remoteId ?: task.id.toLongOrNull()
            val dueDate = task.dueDate.toApiDateOrNull()
            val response = if (remoteId == null) {
                runCatching {
                    api.createTask(
                        deliverableId,
                        CreateTaskItemRequest(
                            title = task.title,
                            dueDate = dueDate
                        )
                    )
                }.getOrNull()
            } else {
                runCatching {
                    if (task.status == Status.FINISHED) {
                        api.finishTask(remoteId)
                    } else {
                        api.updateTask(
                            remoteId,
                            UpdateTaskItemRequest(
                                title = task.title,
                                status = task.status.toWorkStatus(),
                                dueDate = dueDate
                            )
                        )
                    }
                }.getOrNull()
            } ?: return@map task

            response.toTaskItem()
        }

    private suspend fun hasBackendSession(): Boolean =
        !userPreferencesRepository.accessToken.first().isNullOrBlank()

    private fun ProjectResponse.toGroupProject(
        members: List<Member>,
        deliverables: List<Deliverable>,
        localId: String? = null
    ): GroupProject = GroupProject(
        id = localId ?: id.toString(),
        title = name,
        status = status.toStatus(pastDue),
        pastDue = pastDue,
        dueDate = dueDate.toDisplayDate(),
        members = members,
        deliverables = deliverables,
        remoteId = id
    )

    private fun DeliverableResponse.toDeliverable(tasks: List<TaskItem>): Deliverable = Deliverable(
        id = id.toString(),
        title = title,
        description = description.orEmpty(),
        status = status.toStatus(pastDue),
        pastDue = pastDue,
        dueDate = dueDate.toDisplayDate(),
        taskItems = tasks,
        remoteId = id
    )

    private fun TaskItemResponse.toTaskItem(): TaskItem = TaskItem(
        id = id.toString(),
        title = title,
        status = status.toStatus(pastDue),
        pastDue = pastDue,
        dueDate = dueDate?.toDisplayDate().orEmpty(),
        remoteId = id
    )

    private fun ProjectMemberResponse.toMember(): Member = Member(
        id = id.toString(),
        name = user.displayName,
        username = user.email,
        role = role.toRole(),
        remoteMembershipId = id,
        remoteUserId = user.id
    )

    private fun List<UserResponse>.findByMember(member: Member): UserResponse? =
        firstOrNull { user ->
            user.email.equals(member.username, ignoreCase = true) ||
                user.displayName.equals(member.username, ignoreCase = true) ||
                user.displayName.equals(member.name, ignoreCase = true)
        }

    private fun Status.toWorkStatus(): WorkStatus =
        if (this == Status.FINISHED) WorkStatus.FINISHED else WorkStatus.PENDING

    private fun WorkStatus.toStatus(pastDue: Boolean): Status =
        when {
            this == WorkStatus.FINISHED -> Status.FINISHED
            pastDue -> Status.OVERDUE
            else -> Status.PENDING
        }

    private fun Role.toProjectRole(): ProjectRole =
        if (this == Role.OWNER) ProjectRole.OWNER else ProjectRole.MEMBER

    private fun ProjectRole.toRole(): Role =
        if (this == ProjectRole.OWNER) Role.OWNER else Role.MEMBER
}
