package com.example.taskmanager.data.local.dao

import androidx.room.*
import com.example.taskmanager.data.local.entity.GroupProjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupProjectDao {

    @Query("SELECT * FROM group_projects ORDER BY dueDate ASC")
    fun getAllProjects(): Flow<List<GroupProjectEntity>>

    @Query("SELECT * FROM group_projects WHERE id = :projectId")
    suspend fun getProjectById(projectId: String): GroupProjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(project: GroupProjectEntity)

    @Update
    suspend fun update(project: GroupProjectEntity)

    @Delete
    suspend fun delete(project: GroupProjectEntity)
}
