package com.example.makeitso.data.model

import com.google.firebase.firestore.DocumentId
import java.util.Date

data class TodoItem(
    @DocumentId val id: String = "",
    val title: String = "",
    val priority: Int = Priority.NONE.value,
    val completed: Boolean = false,
    val flagged: Boolean = false,
    val deadline: Deadline = Deadline.NONE,
    val createdAt: Date = Date(),
    val completedAt: Date? = null,
    val ownerId: String = ""
)

val TodoItem.isHighPriority: Boolean
    get() = this.priority == Priority.HIGH.value

val TodoItem.isMediumPriority: Boolean
    get() = this.priority == Priority.MEDIUM.value

val TodoItem.isLowPriority: Boolean
    get() = this.priority == Priority.LOW.value

val TodoItem.isNonePriority: Boolean
    get() = this.priority == Priority.NONE.value

