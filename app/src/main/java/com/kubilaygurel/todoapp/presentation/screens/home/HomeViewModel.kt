package com.kubilaygurel.todoapp.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kubilaygurel.todoapp.domain.repository.TaskRepository
import com.kubilaygurel.todoapp.domain.model.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {
    val activeTask: Flow<List<Task>> = repository.getActiveTask()
        .catch { e -> e.printStackTrace() }
    
    fun deleteNote(task: Task) = viewModelScope.launch {
        try {
            repository.deleteTask(task)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateTaskCompletion(task: Task, isCompleted: Boolean) = viewModelScope.launch {
        try {
            repository.updateTask(task.copy(isCompleted = isCompleted))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

} 