package com.example.taskmanager.data.repository

import com.example.taskmanager.classes.Tasks
import com.example.taskmanager.data.local.dao.TaskDao
import com.example.taskmanager.data.local.entity.toEntity
import com.example.taskmanager.data.local.entity.toTasks
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskRepository(private val taskDao: TaskDao) {

    val allTasks: Flow<List<Tasks>> = taskDao.getAllTasks().map { list -> list.map { it.toTasks() } }

    suspend fun addTask(task: Tasks) {
        taskDao.insert(task.toEntity())
    }

    suspend fun updateTask(task: Tasks) {
        taskDao.update(task.toEntity())
    }

    suspend fun deleteTask(task: Tasks) {
        taskDao.delete(task.toEntity())
    }

    suspend fun toggleCompleted(task: Tasks) {
        taskDao.update(task.copy(completed = !task.completed).toEntity())
    }
}
