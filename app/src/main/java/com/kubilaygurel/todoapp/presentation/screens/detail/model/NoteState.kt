package com.kubilaygurel.todoapp.presentation.screens.detail.model

import java.util.Date

data class NoteState(
    val title: String = "",
    val content: String = "",
    val reminderTime: Date? = null,
    val imageUri: String? = null,
    val isLoading: Boolean = false
)