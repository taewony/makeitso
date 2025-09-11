package com.example.makeitso.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ai_messages")
data class AiMessageEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val prompt: String,
    val response: String,
    val character: String, // AiCharacter enum을 String으로 저장
    val createdAt: Long,
    val triggerType: String // TriggerType enum을 String으로 저장
)