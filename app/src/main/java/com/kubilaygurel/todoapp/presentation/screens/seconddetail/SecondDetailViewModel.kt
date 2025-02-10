package com.kubilaygurel.todoapp.presentation.screens.seconddetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.kubilaygurel.todoapp.domain.model.Task
import com.kubilaygurel.todoapp.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class SecondDetailViewModel @Inject constructor(
    repository: TaskRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val taskId: Int = checkNotNull(savedStateHandle["taskId"]).toString().toInt()
    
    val task: Flow<Task?> = repository.getCompletedTasks()
        .map { tasks -> tasks.find { it.id == taskId } }
} 