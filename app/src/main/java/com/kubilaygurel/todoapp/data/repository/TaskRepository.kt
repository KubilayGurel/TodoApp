package com.kubilaygurel.todoapp.data.repository

import com.kubilaygurel.todoapp.data.database.TaskDao
import com.kubilaygurel.todoapp.domain.model.Task
import com.kubilaygurel.todoapp.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TaskRepository @Inject constructor(
    private val taskDao: TaskDao
) : TaskRepository {
     override fun getActiveTask(): Flow<List<Task>> = taskDao.getActiveTask()

    override fun getCompletedTasks(): Flow<List<Task>> = taskDao.getCompletedTask()

    override suspend fun insertTask(task: Task) = taskDao.insertTask(task)

    override suspend fun updateTask(task: Task) = taskDao.updateTask(task)

    override suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)

    override suspend fun getTaskById(id: Int): Task? = taskDao.getTaskById(id)
}