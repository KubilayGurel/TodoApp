package com.kubilaygurel.todoapp.data.repository

import com.kubilaygurel.todoapp.data.database.TaskDao
import com.kubilaygurel.todoapp.domain.model.Task
import com.kubilaygurel.todoapp.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao
) : TaskRepository {
    override fun getActiveTask(): Flow<List<Task>> =
        try {
            taskDao.getActiveTask()
        } catch (e: Exception) {

            throw e
        }

    override fun getCompletedTasks(): Flow<List<Task>> = taskDao.getCompletedTask()

    override suspend fun insertTask(task: Task) =
        try {
            taskDao.insertTask(task)
        } catch (e: Exception) {

            throw e
        }

    override suspend fun updateTask(task: Task) = taskDao.updateTask(task)
    override suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)

    override suspend fun getTaskById(id: Int): Task? =
        try {
            taskDao.getTaskById(id)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
} 