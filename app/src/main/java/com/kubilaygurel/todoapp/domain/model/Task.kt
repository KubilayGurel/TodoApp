package com.kubilaygurel.todoapp.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date


@Entity(tableName = "task")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val date: Date,
    val isCompleted: Boolean = false,
    val reminderTime: Date? = null,
    val imageUri: String? = null
)
