package com.kubilaygurel.todoapp.presentation.screens.completed

import androidx.lifecycle.ViewModel
import com.kubilaygurel.todoapp.domain.repository.TaskRepository
import com.kubilaygurel.todoapp.domain.model.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope

@HiltViewModel
class CompletedViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {
    val completedNotes: Flow<List<Task>> = repository.getCompletedTasks()
    
    fun deleteTask(task: Task) = viewModelScope.launch {
        try {
            repository.deleteTask(task)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
} 