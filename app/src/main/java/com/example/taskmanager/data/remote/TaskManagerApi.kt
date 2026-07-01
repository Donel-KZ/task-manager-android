package com.example.taskmanager.data.remote

import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class RegisterRequest(
    val displayName: String,
    val email: String,
    val password: String
)

@Serializable
data class ForgotPasswordRequest(
    val email: String
)

@Serializable
data class ResetPasswordRequest(
    val token: String,
    val password: String
)

@Serializable
data class MessageResponse(
    val message: String
)

@Serializable
data class ApiErrorResponse(
    val message: String? = null,
    val errors: Map<String, String> = emptyMap()
)

@Serializable
data class AuthResponse(
    val tokenType: String,
    val accessToken: String,
    val user: UserResponse
)

@Serializable
data class UserResponse(
    val id: Long,
    val displayName: String,
    val email: String
)

@Serializable
enum class ProjectType {
    INDIVIDUAL,
    GROUP
}

@Serializable
enum class WorkStatus {
    PENDING,
    FINISHED
}

@Serializable
enum class ProjectRole {
    OWNER,
    MEMBER
}

@Serializable
data class CreateProjectRequest(
    val name: String,
    val description: String? = null,
    val type: ProjectType,
    val dueDate: String,
    val ownerId: Long
)

@Serializable
data class UpdateProjectRequest(
    val name: String,
    val description: String? = null,
    val type: ProjectType,
    val status: WorkStatus,
    val dueDate: String
)

@Serializable
data class ProjectResponse(
    val id: Long,
    val name: String,
    val description: String? = null,
    val type: ProjectType,
    val status: WorkStatus,
    val dueDate: String,
    val pastDue: Boolean,
    val owner: UserResponse
)

@Serializable
data class ProjectMemberResponse(
    val id: Long,
    val user: UserResponse,
    val role: ProjectRole
)

@Serializable
data class AddMemberRequest(
    val userId: Long,
    val role: ProjectRole
)

@Serializable
data class CreateDeliverableRequest(
    val title: String,
    val description: String? = null,
    val dueDate: String
)

@Serializable
data class UpdateDeliverableRequest(
    val title: String,
    val description: String? = null,
    val status: WorkStatus,
    val dueDate: String
)

@Serializable
data class DeliverableResponse(
    val id: Long,
    val projectId: Long,
    val title: String,
    val description: String? = null,
    val status: WorkStatus,
    val dueDate: String,
    val pastDue: Boolean
)

@Serializable
data class CreateTaskItemRequest(
    val title: String,
    val dueDate: String? = null
)

@Serializable
data class UpdateTaskItemRequest(
    val title: String,
    val status: WorkStatus,
    val dueDate: String? = null
)

@Serializable
data class TaskItemResponse(
    val id: Long,
    val deliverableId: Long,
    val title: String,
    val status: WorkStatus,
    val dueDate: String? = null,
    val pastDue: Boolean
)

interface TaskManagerApi {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("api/auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): MessageResponse

    @POST("api/auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): MessageResponse

    @GET("api/users")
    suspend fun getUsers(): List<UserResponse>

    @GET("api/projects")
    suspend fun getProjects(): List<ProjectResponse>

    @GET("api/projects/{id}")
    suspend fun getProject(@Path("id") id: Long): ProjectResponse

    @POST("api/projects")
    suspend fun createProject(@Body request: CreateProjectRequest): ProjectResponse

    @PUT("api/projects/{id}")
    suspend fun updateProject(
        @Path("id") id: Long,
        @Body request: UpdateProjectRequest
    ): ProjectResponse

    @PATCH("api/projects/{id}/finish")
    suspend fun finishProject(@Path("id") id: Long): ProjectResponse

    @GET("api/projects/{id}/members")
    suspend fun getProjectMembers(@Path("id") id: Long): List<ProjectMemberResponse>

    @POST("api/projects/{id}/members")
    suspend fun addProjectMember(
        @Path("id") id: Long,
        @Body request: AddMemberRequest
    ): ProjectMemberResponse

    @GET("api/projects/{projectId}/deliverables")
    suspend fun getDeliverables(@Path("projectId") projectId: Long): List<DeliverableResponse>

    @POST("api/projects/{projectId}/deliverables")
    suspend fun createDeliverable(
        @Path("projectId") projectId: Long,
        @Body request: CreateDeliverableRequest
    ): DeliverableResponse

    @PUT("api/deliverables/{id}")
    suspend fun updateDeliverable(
        @Path("id") id: Long,
        @Body request: UpdateDeliverableRequest
    ): DeliverableResponse

    @PATCH("api/deliverables/{id}/finish")
    suspend fun finishDeliverable(@Path("id") id: Long): DeliverableResponse

    @GET("api/deliverables/{deliverableId}/tasks")
    suspend fun getTasks(@Path("deliverableId") deliverableId: Long): List<TaskItemResponse>

    @POST("api/deliverables/{deliverableId}/tasks")
    suspend fun createTask(
        @Path("deliverableId") deliverableId: Long,
        @Body request: CreateTaskItemRequest
    ): TaskItemResponse

    @PUT("api/tasks/{id}")
    suspend fun updateTask(
        @Path("id") id: Long,
        @Body request: UpdateTaskItemRequest
    ): TaskItemResponse

    @PATCH("api/tasks/{id}/finish")
    suspend fun finishTask(@Path("id") id: Long): TaskItemResponse
}
