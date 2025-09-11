package com.example.makeitso.data.dao

import androidx.room.*
import com.example.makeitso.data.model.entity.AiMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AiMessageDao {
    
    @Query("SELECT * FROM ai_messages WHERE userId = :userId ORDER BY createdAt DESC")
    fun getAiMessagesFlow(userId: String): Flow<List<AiMessageEntity>>
    
    @Query("SELECT * FROM ai_messages WHERE id = :messageId")
    suspend fun getAiMessage(messageId: String): AiMessageEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAiMessage(message: AiMessageEntity)
    
    @Update
    suspend fun updateAiMessage(message: AiMessageEntity)
    
    @Query("DELETE FROM ai_messages WHERE id = :messageId")
    suspend fun deleteAiMessage(messageId: String)
    
    @Query("DELETE FROM ai_messages WHERE userId = :userId")
    suspend fun deleteAllAiMessages(userId: String)
    
    @Query("SELECT * FROM ai_messages WHERE userId = :userId AND triggerType = :triggerType ORDER BY createdAt DESC")
    fun getAiMessagesByType(userId: String, triggerType: String): Flow<List<AiMessageEntity>>
    
    @Query("SELECT * FROM ai_messages WHERE userId = :userId AND character = :character ORDER BY createdAt DESC")
    fun getAiMessagesByCharacter(userId: String, character: String): Flow<List<AiMessageEntity>>
}