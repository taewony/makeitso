package com.example.makeitso.data.datasource

import com.example.makeitso.data.model.AiMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiAssistantLocalDataSource @Inject constructor() {
    private val _aiMessages = MutableStateFlow<List<AiMessage>>(emptyList())
    
    fun getAiMessagesFlow(userId: String): Flow<List<AiMessage>> {
        return _aiMessages.asStateFlow().map { messages ->
            messages.filter { it.userId == userId }
                .sortedByDescending { it.createdAt }
        }
    }

    suspend fun saveAiMessage(message: AiMessage): String {
        val messageWithId = if (message.id.isEmpty()) {
            message.copy(id = UUID.randomUUID().toString())
        } else {
            message
        }

        val currentMessages = _aiMessages.value.toMutableList()
        val existingIndex = currentMessages.indexOfFirst { it.id == messageWithId.id }
        
        if (existingIndex >= 0) {
            currentMessages[existingIndex] = messageWithId
        } else {
            currentMessages.add(messageWithId)
        }
        
        _aiMessages.value = currentMessages
        return messageWithId.id
    }

    suspend fun getAiMessage(messageId: String): AiMessage? {
        return _aiMessages.value.find { it.id == messageId }
    }

    suspend fun clearAiMessages(userId: String) {
        val currentMessages = _aiMessages.value.toMutableList()
        currentMessages.removeAll { it.userId == userId }
        _aiMessages.value = currentMessages
    }
}