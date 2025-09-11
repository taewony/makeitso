package com.example.makeitso.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.makeitso.data.model.Deadline

@Entity(tableName = "todo_items")
data class TodoItemEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val priority: Int,
    val completed: Boolean,
    val flagged: Boolean,
    val deadline: String, // Deadline enum을 String으로 저장
    val createdAt: Long,
    val completedAt: Long?,
    val ownerId: String
)