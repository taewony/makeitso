package com.example.makeitso.ui.settings

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
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userProfileRepository: UserProfileRepository
) : MainViewModel() {
    private val _shouldRestartApp = MutableStateFlow(false)
    val shouldRestartApp: StateFlow<Boolean>
        get() = _shouldRestartApp.asStateFlow()

    private val _isAnonymous = MutableStateFlow(true)
    val isAnonymous: StateFlow<Boolean>
        get() = _isAnonymous.asStateFlow()

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    private val _showGoalsDialog = MutableStateFlow(false)
    val showGoalsDialog: StateFlow<Boolean> = _showGoalsDialog.asStateFlow()

    private val _showCharacterDialog = MutableStateFlow(false)
    val showCharacterDialog: StateFlow<Boolean> = _showCharacterDialog.asStateFlow()

    private val _showHistoryDialog = MutableStateFlow(false)
    val showHistoryDialog: StateFlow<Boolean> = _showHistoryDialog.asStateFlow()

    fun loadCurrentUser() {
        launchCatching {
            val userId = authRepository.getCurrentUserId()
            _isAnonymous.value = false // Phase 1에서는 항상 false
            
            userId?.let { id ->
                val profile = userProfileRepository.getUserProfile(id)
                _userProfile.value = profile
            }
        }
    }

    fun showGoalsDialog() {
        _showGoalsDialog.value = true
    }

    fun hideGoalsDialog() {
        _showGoalsDialog.value = false
    }

    fun showCharacterDialog() {
        _showCharacterDialog.value = true
    }

    fun hideCharacterDialog() {
        _showCharacterDialog.value = false
    }

    fun showHistoryDialog() {
        _showHistoryDialog.value = true
    }

    fun hideHistoryDialog() {
        _showHistoryDialog.value = false
    }

    fun updateGoals(shortTermGoal: String, longTermGoal: String) {
        launchCatching {
            val currentProfile = _userProfile.value ?: return@launchCatching
            val updatedProfile = currentProfile.copy(
                goals = UserGoals(shortTermGoal, longTermGoal)
            )
            userProfileRepository.updateUserProfile(updatedProfile)
            _userProfile.value = updatedProfile
            _showGoalsDialog.value = false
        }
    }

    fun updateCharacter(character: AiCharacter) {
        launchCatching {
            val currentProfile = _userProfile.value ?: return@launchCatching
            val updatedProfile = currentProfile.copy(selectedCharacter = character)
            userProfileRepository.updateUserProfile(updatedProfile)
            _userProfile.value = updatedProfile
            _showCharacterDialog.value = false
        }
    }

    fun signOut() {
        launchCatching {
            authRepository.getCurrentUserId()?.let { userId ->
                userProfileRepository.clearUserProfile()
            }
            authRepository.signOut()
            _shouldRestartApp.value = true
        }
    }

    fun deleteAccount() {
        launchCatching {
            authRepository.getCurrentUserId()?.let { userId ->
                userProfileRepository.clearUserProfile()
            }
            authRepository.deleteAccount()
            _shouldRestartApp.value = true
        }
    }
}
