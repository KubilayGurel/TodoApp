package com.kubilaygurel.todoapp.presentation.screens.detail

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.kubilaygurel.todoapp.data.util.NotificationWorker
import com.kubilaygurel.todoapp.domain.repository.TaskRepository
import com.kubilaygurel.todoapp.domain.model.Task
import com.kubilaygurel.todoapp.presentation.screens.detail.model.NoteState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: TaskRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _noteState = MutableStateFlow(NoteState())
    val noteState: StateFlow<NoteState> = _noteState.asStateFlow()

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState

    fun loadNote(noteId: Int) = viewModelScope.launch {
        if (noteId == 0) {
            _noteState.update { it.copy(isLoading = false) }
            return@launch
        }
        try {
            _noteState.update { it.copy(isLoading = true) }
            repository.getTaskById(noteId)?.let { task ->
                _noteState.update {
                    it.copy(
                        title = task.title,
                        content = task.content,
                        reminderTime = task.reminderTime,
                        isLoading = false
                    )
                }
            } ?: run {
                _errorState.value = "Task not found"
                _noteState.update { it.copy(isLoading = false) }
            }
        } catch (e: Exception) {
            _errorState.value = e.message ?: "An error occurred while loading the note task"
            _noteState.update { it.copy(isLoading = false) }
        }
    }

    private fun updateNote(task: Task) = viewModelScope.launch {
        try {
            _noteState.update { it.copy(isLoading = true) }
            repository.updateTask(task)
            task.reminderTime?.let { scheduleNotification(task) }
        } catch (e: Exception) {
            _errorState.value = e.message ?: "An error occurred during the update"
        } finally {
            _noteState.update { it.copy(isLoading = false) }
        }
    }

    private fun insertNote(task: Task) = viewModelScope.launch {
        try {
            _noteState.update { it.copy(isLoading = true) }
            repository.insertTask(task)
            task.reminderTime?.let { scheduleNotification(task) }
        } catch (e: Exception) {
            _errorState.value = e.message ?: "An error occurred while saving"
        } finally {
            _noteState.update { it.copy(isLoading = false) }
        }
    }

    private fun scheduleNotification(task: Task) {
        if (!hasNotificationPermission()) {
            _errorState.value = "Notification permission not granted"
            return
        }
        try {
            val workManager = WorkManager.getInstance(context)
            val data = workDataOf(
                "title" to task.title,
                "content" to task.content
            )

            val delay = task.reminderTime?.time?.minus(System.currentTimeMillis()) ?: 0L
            if (delay <= 0) {
                _errorState.value = "Reminder time is in the past"
                return
            }

            val notificationWork = OneTimeWorkRequestBuilder<NotificationWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .build()

            workManager.enqueueUniqueWork(
                "notification_${task.id}",
                ExistingWorkPolicy.REPLACE,
                notificationWork
            )
        } catch (e: SecurityException) {
            _errorState.value = "Notification scheduling failed: ${e.message}"
        }
    }

    private fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun setError(message: String) {
        _errorState.value = message
    }

    fun clearError() {
        _errorState.value = null
    }

    fun updateTitle(title: String) {
        _noteState.update { it.copy(title = title) }
    }

    fun updateContent(content: String) {
        _noteState.update { it.copy(content = content) }
    }

    fun updateImageUri(uri: String?) {
        _noteState.update { it.copy(imageUri = uri) }
    }

    fun updateReminderTime(date: Date?) {
        _noteState.update { it.copy(reminderTime = date) }
    }

    fun saveNote(noteId: Int) = viewModelScope.launch {
        try {
            val currentTask = repository.getTaskById(noteId)
            val task = Task(
                id = noteId,
                title = noteState.value.title,
                content = noteState.value.content,
                date = Date(),
                reminderTime = noteState.value.reminderTime,
                imageUri = noteState.value.imageUri,
                isCompleted = currentTask?.isCompleted ?: false
            )

            if (noteId == 0) {
                insertNote(task)
            } else {
                updateNote(task)
            }
        } catch (e: Exception) {
            setError(e.message ?: "An error occurred while saving")
        }
    }
}
