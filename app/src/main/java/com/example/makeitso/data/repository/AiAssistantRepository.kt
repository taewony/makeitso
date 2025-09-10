package com.example.makeitso.data.repository

import com.example.makeitso.data.datasource.AiAssistantLocalDataSource
import com.example.makeitso.data.model.AiCharacter
import com.example.makeitso.data.model.AiMessage
import com.example.makeitso.data.model.TodoItem
import com.example.makeitso.data.model.TriggerType
import com.example.makeitso.data.model.UserGoals
import com.example.makeitso.service.AiPromptService
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiAssistantRepository @Inject constructor(
    private val aiAssistantLocalDataSource: AiAssistantLocalDataSource,
    private val aiPromptService: AiPromptService
) {
    fun getAiMessagesFlow(userId: String): Flow<List<AiMessage>> {
        return aiAssistantLocalDataSource.getAiMessagesFlow(userId)
    }

    suspend fun generateAiResponse(
        userId: String,
        goals: UserGoals,
        todoItems: List<TodoItem>,
        character: AiCharacter,
        triggerType: TriggerType
    ): AiMessage {
        val prompt = aiPromptService.generatePrompt(goals, todoItems, character, triggerType)
        val response = aiPromptService.generateResponse(goals, todoItems, character, triggerType)
        
        val aiMessage = AiMessage(
            userId = userId,
            prompt = prompt,
            response = response,
            character = character,
            createdAt = Date(),
            triggerType = triggerType
        )
        
        val messageId = aiAssistantLocalDataSource.saveAiMessage(aiMessage)
        return aiMessage.copy(id = messageId)
    }

    suspend fun saveAiMessage(message: AiMessage): String {
        return aiAssistantLocalDataSource.saveAiMessage(message)
    }

    suspend fun getAiMessage(messageId: String): AiMessage? {
        return aiAssistantLocalDataSource.getAiMessage(messageId)
    }

    suspend fun clearAiMessages(userId: String) {
        aiAssistantLocalDataSource.clearAiMessages(userId)
    }
}