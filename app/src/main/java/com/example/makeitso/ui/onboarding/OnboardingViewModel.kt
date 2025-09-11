package com.example.makeitso.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.makeitso.MainViewModel
import com.example.makeitso.data.model.AiCharacter
import com.example.makeitso.data.model.UserGoals
import com.example.makeitso.data.model.UserProfile
import com.example.makeitso.data.repository.AuthRepository
import com.example.makeitso.data.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnboardingUiState(
    val shortTermGoal: String = "",
    val longTermGoal: String = "",
    val selectedCharacter: AiCharacter = AiCharacter.HARSH_CRITIC,
    val isLoading: Boolean = false,
    val isComplete: Boolean = false
) {
    val canComplete: Boolean
        get() = shortTermGoal.isNotBlank() && longTermGoal.isNotBlank()
}

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userProfileRepository: UserProfileRepository
) : MainViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun updateShortTermGoal(goal: String) {
        _uiState.value = _uiState.value.copy(shortTermGoal = goal)
    }

    fun updateLongTermGoal(goal: String) {
        _uiState.value = _uiState.value.copy(longTermGoal = goal)
    }

    fun selectCharacter(character: AiCharacter) {
        _uiState.value = _uiState.value.copy(selectedCharacter = character)
    }

    fun completeOnboarding() {
        val currentState = _uiState.value
        println("OnboardingViewModel: completeOnboarding called, canComplete=${currentState.canComplete}")
        
        if (!currentState.canComplete) {
            println("OnboardingViewModel: Cannot complete - goals not filled")
            return
        }

        launchCatching {
            println("OnboardingViewModel: Starting onboarding completion")
            _uiState.value = currentState.copy(isLoading = true)

            try {
                val userId = authRepository.getCurrentUserId()
                println("OnboardingViewModel: Current user ID = $userId")
                
                if (userId == null) {
                    println("OnboardingViewModel: ERROR - No current user found")
                    _uiState.value = currentState.copy(isLoading = false)
                    return@launchCatching
                }
                
                val userProfile = UserProfile(
                    userId = userId,
                    goals = UserGoals(
                        shortTermGoal = currentState.shortTermGoal,
                        longTermGoal = currentState.longTermGoal
                    ),
                    selectedCharacter = currentState.selectedCharacter,
                    isOnboardingComplete = true
                )

                println("OnboardingViewModel: Creating user profile: $userProfile")
                userProfileRepository.createUserProfile(userProfile)
                println("OnboardingViewModel: User profile created successfully")
                
                // 잠시 대기 후 완료 상태로 변경 (UI 업데이트 보장)
                kotlinx.coroutines.delay(500)
                
                _uiState.value = currentState.copy(
                    isLoading = false,
                    isComplete = true
                )
                println("OnboardingViewModel: Onboarding marked as complete")
            } catch (e: Exception) {
                println("OnboardingViewModel: Error during onboarding completion: ${e.message}")
                e.printStackTrace()
                _uiState.value = currentState.copy(isLoading = false)
            }
        }
    }
}