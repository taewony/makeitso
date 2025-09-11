package com.example.makeitso.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.makeitso.data.model.AiCharacter

@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey
    val userId: String,
    val shortTermGoal: String,
    val longTermGoal: String,
    val selectedCharacter: String, // AiCharacter enum을 String으로 저장
    val isOnboardingComplete: Boolean,
    val loginTimestamp: Long = System.currentTimeMillis(),
    val sessionExpiryTimestamp: Long = System.currentTimeMillis() + (4 * 7 * 24 * 60 * 60 * 1000L) // 4주
)