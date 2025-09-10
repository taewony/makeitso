package com.example.makeitso.data.datasource

import android.content.SharedPreferences
import com.example.makeitso.data.model.AiCharacter
import com.example.makeitso.data.model.UserGoals
import com.example.makeitso.data.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfileLocalDataSource @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {
    private val _userProfileFlow = MutableStateFlow<UserProfile?>(null)
    val userProfileFlow: Flow<UserProfile?> = _userProfileFlow.asStateFlow()

    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_SHORT_TERM_GOAL = "short_term_goal"
        private const val KEY_LONG_TERM_GOAL = "long_term_goal"
        private const val KEY_SELECTED_CHARACTER = "selected_character"
        private const val KEY_ONBOARDING_COMPLETE = "onboarding_complete"
    }

    suspend fun getUserProfile(userId: String): UserProfile? {
        val storedUserId = sharedPreferences.getString(KEY_USER_ID, null)
        if (storedUserId != userId) return null

        val shortTermGoal = sharedPreferences.getString(KEY_SHORT_TERM_GOAL, "") ?: ""
        val longTermGoal = sharedPreferences.getString(KEY_LONG_TERM_GOAL, "") ?: ""
        val characterName = sharedPreferences.getString(KEY_SELECTED_CHARACTER, AiCharacter.HARSH_CRITIC.name)
        val isOnboardingComplete = sharedPreferences.getBoolean(KEY_ONBOARDING_COMPLETE, false)

        val character = try {
            AiCharacter.valueOf(characterName ?: AiCharacter.HARSH_CRITIC.name)
        } catch (e: IllegalArgumentException) {
            AiCharacter.HARSH_CRITIC
        }

        val profile = UserProfile(
            userId = userId,
            goals = UserGoals(shortTermGoal, longTermGoal),
            selectedCharacter = character,
            isOnboardingComplete = isOnboardingComplete
        )

        _userProfileFlow.value = profile
        return profile
    }

    suspend fun saveUserProfile(profile: UserProfile) {
        println("UserProfileLocalDataSource: Saving profile: $profile")
        with(sharedPreferences.edit()) {
            putString(KEY_USER_ID, profile.userId)
            putString(KEY_SHORT_TERM_GOAL, profile.goals.shortTermGoal)
            putString(KEY_LONG_TERM_GOAL, profile.goals.longTermGoal)
            putString(KEY_SELECTED_CHARACTER, profile.selectedCharacter.name)
            putBoolean(KEY_ONBOARDING_COMPLETE, profile.isOnboardingComplete)
            apply()
        }
        _userProfileFlow.value = profile
        println("UserProfileLocalDataSource: Profile saved successfully, flow updated")
    }

    suspend fun clearUserProfile() {
        with(sharedPreferences.edit()) {
            remove(KEY_USER_ID)
            remove(KEY_SHORT_TERM_GOAL)
            remove(KEY_LONG_TERM_GOAL)
            remove(KEY_SELECTED_CHARACTER)
            remove(KEY_ONBOARDING_COMPLETE)
            apply()
        }
        _userProfileFlow.value = null
    }
}