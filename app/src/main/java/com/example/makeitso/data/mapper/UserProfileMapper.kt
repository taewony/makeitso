package com.example.makeitso.data.mapper

import com.example.makeitso.data.model.AiCharacter
import com.example.makeitso.data.model.UserGoals
import com.example.makeitso.data.model.UserProfile
import com.example.makeitso.data.model.entity.UserProfileEntity

object UserProfileMapper {
    
    fun toEntity(userProfile: UserProfile): UserProfileEntity {
        return UserProfileEntity(
            userId = userProfile.userId,
            shortTermGoal = userProfile.goals.shortTermGoal,
            longTermGoal = userProfile.goals.longTermGoal,
            selectedCharacter = userProfile.selectedCharacter.name,
            isOnboardingComplete = userProfile.isOnboardingComplete,
            loginTimestamp = System.currentTimeMillis(),
            sessionExpiryTimestamp = System.currentTimeMillis() + (4 * 7 * 24 * 60 * 60 * 1000L)
        )
    }
    
    fun toDomain(entity: UserProfileEntity): UserProfile {
        return UserProfile(
            userId = entity.userId,
            goals = UserGoals(
                shortTermGoal = entity.shortTermGoal,
                longTermGoal = entity.longTermGoal
            ),
            selectedCharacter = try {
                AiCharacter.valueOf(entity.selectedCharacter)
            } catch (e: IllegalArgumentException) {
                AiCharacter.HARSH_CRITIC // 기본값
            },
            isOnboardingComplete = entity.isOnboardingComplete
        )
    }
    
    fun isSessionValid(entity: UserProfileEntity): Boolean {
        return System.currentTimeMillis() < entity.sessionExpiryTimestamp
    }
}