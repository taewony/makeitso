package com.example.makeitso.data.datasource

import com.example.makeitso.data.dao.AiMessageDao
import com.example.makeitso.data.mapper.AiMessageMapper
import com.example.makeitso.data.model.AiMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiAssistantLocalDataSource @Inject constructor(
    private val aiMessageDao: AiMessageDao
) {
    
    fun getAiMessagesFlow(userId: String): Flow<List<AiMessage>> {
        return aiMessageDao.getAiMessagesFlow(userId).map { entities ->
            entities.map { AiMessageMapper.toDomain(it) }
        }
    }

    suspend fun saveAiMessage(message: AiMessage): String {
        println("AiAssistantLocalDataSource: Saving AI message: ${message.response.take(50)}...")
        
        val messageWithId = if (message.id.isEmpty()) {
            message.copy(id = UUID.randomUUID().toString())
        } else {
            message
        }

        val entity = AiMessageMapper.toEntity(messageWithId)
        aiMessageDao.insertAiMessage(entity)
        
        println("AiAssistantLocalDataSource: AI message saved to Room DB with ID: ${messageWithId.id}")
        return messageWithId.id
    }

    suspend fun getAiMessage(messageId: String): AiMessage? {
        val entity = aiMessageDao.getAiMessage(messageId)
        return entity?.let { AiMessageMapper.toDomain(it) }
    }

    suspend fun clearAiMessages(userId: String) {
        println("AiAssistantLocalDataSource: Clearing AI messages for user: $userId")
        aiMessageDao.deleteAllAiMessages(userId)
    }
}