package com.example.makeitso.data.mapper

import com.example.makeitso.data.model.AiCharacter
import com.example.makeitso.data.model.AiMessage
import com.example.makeitso.data.model.TriggerType
import com.example.makeitso.data.model.entity.AiMessageEntity
import java.util.Date

object AiMessageMapper {
    
    fun toEntity(aiMessage: AiMessage): AiMessageEntity {
        return AiMessageEntity(
            id = aiMessage.id,
            userId = aiMessage.userId,
            prompt = aiMessage.prompt,
            response = aiMessage.response,
            character = aiMessage.character.name,
            createdAt = aiMessage.createdAt.time,
            triggerType = aiMessage.triggerType.name
        )
    }
    
    fun toDomain(entity: AiMessageEntity): AiMessage {
        return AiMessage(
            id = entity.id,
            userId = entity.userId,
            prompt = entity.prompt,
            response = entity.response,
            character = try {
                AiCharacter.valueOf(entity.character)
            } catch (e: IllegalArgumentException) {
                AiCharacter.HARSH_CRITIC // 기본값
            },
            createdAt = Date(entity.createdAt),
            triggerType = try {
                TriggerType.valueOf(entity.triggerType)
            } catch (e: IllegalArgumentException) {
                TriggerType.MANUAL // 기본값
            }
        )
    }
}