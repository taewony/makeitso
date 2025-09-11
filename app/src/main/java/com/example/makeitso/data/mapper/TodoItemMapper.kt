package com.example.makeitso.data.mapper

import com.example.makeitso.data.model.Deadline
import com.example.makeitso.data.model.TodoItem
import com.example.makeitso.data.model.entity.TodoItemEntity
import java.util.Date

object TodoItemMapper {
    
    fun toEntity(todoItem: TodoItem): TodoItemEntity {
        return TodoItemEntity(
            id = todoItem.id,
            title = todoItem.title,
            priority = todoItem.priority,
            completed = todoItem.completed,
            flagged = todoItem.flagged,
            deadline = todoItem.deadline.name,
            createdAt = todoItem.createdAt.time,
            completedAt = todoItem.completedAt?.time,
            ownerId = todoItem.ownerId
        )
    }
    
    fun toDomain(entity: TodoItemEntity): TodoItem {
        return TodoItem(
            id = entity.id,
            title = entity.title,
            priority = entity.priority,
            completed = entity.completed,
            flagged = entity.flagged,
            deadline = try {
                Deadline.valueOf(entity.deadline)
            } catch (e: IllegalArgumentException) {
                Deadline.NONE // 기본값
            },
            createdAt = Date(entity.createdAt),
            completedAt = entity.completedAt?.let { Date(it) },
            ownerId = entity.ownerId
        )
    }
}