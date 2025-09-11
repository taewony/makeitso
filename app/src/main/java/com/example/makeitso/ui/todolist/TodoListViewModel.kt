package com.example.makeitso.ui.todolist

import com.example.makeitso.MainViewModel
import com.example.makeitso.data.model.AiCharacter
import com.example.makeitso.data.model.TodoItem
import com.example.makeitso.data.model.TriggerType
import com.example.makeitso.data.model.needsOnboarding
import com.example.makeitso.data.repository.AiAssistantRepository
import com.example.makeitso.data.repository.AuthRepository
import com.example.makeitso.data.repository.TodoItemRepository
import com.example.makeitso.data.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class TodoListViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val todoItemRepository: TodoItemRepository,
    private val userProfileRepository: UserProfileRepository,
    private val aiAssistantRepository: AiAssistantRepository
) : MainViewModel() {
    private val _isLoadingUser = MutableStateFlow(true)
    val isLoadingUser: StateFlow<Boolean>
        get() = _isLoadingUser.asStateFlow()

    val todoItems = todoItemRepository.getTodoItems(authRepository.currentUserIdFlow)

    private val _showAiNudgeDialog = MutableStateFlow(false)
    val showAiNudgeDialog: StateFlow<Boolean>
        get() = _showAiNudgeDialog.asStateFlow()

    private val _aiNudgeMessage = MutableStateFlow("")
    val aiNudgeMessage: StateFlow<String>
        get() = _aiNudgeMessage.asStateFlow()

    private val _needsOnboarding = MutableStateFlow(false)
    val needsOnboarding: StateFlow<Boolean>
        get() = _needsOnboarding.asStateFlow()

    private val _needsSignUp = MutableStateFlow(false)
    val needsSignUp: StateFlow<Boolean>
        get() = _needsSignUp.asStateFlow()

    private val _needsSignIn = MutableStateFlow(false)
    val needsSignIn: StateFlow<Boolean>
        get() = _needsSignIn.asStateFlow()

    fun onNudgeButtonClick() {
        launchCatching {
            val userId = authRepository.getCurrentUserId() ?: return@launchCatching
            val userProfile = userProfileRepository.getUserProfile(userId) ?: return@launchCatching
            
            // 현재 TODO 아이템들을 직접 가져오기
            val currentTodoItems = todoItemRepository.getAllTodoItems(userId)
            
            val aiMessage = aiAssistantRepository.generateAiResponse(
                userId = userId,
                goals = userProfile.goals,
                todoItems = currentTodoItems,
                character = userProfile.selectedCharacter,
                triggerType = TriggerType.MANUAL
            )
            
            _aiNudgeMessage.value = aiMessage.response
            _showAiNudgeDialog.value = true
        }
    }

    fun onDialogDismiss() {
        _showAiNudgeDialog.value = false
        _aiNudgeMessage.value = ""
    }

    fun loadCurrentUser() {
        launchCatching {
            val userId = authRepository.getCurrentUserId()
            
            if (userId == null) {
                // 로그아웃한 사용자인지 최초 사용자인지 확인
                if (authRepository.hasUserSignedOut()) {
                    // 로그아웃한 사용자 → SignIn 화면으로
                    _needsSignIn.value = true
                    _needsSignUp.value = false
                } else {
                    // 최초 설치인지 기존 사용자인지 확인
                    val hasExistingUserData = checkForExistingUserData()
                    if (hasExistingUserData) {
                        // 기존 사용자 → SignIn 화면으로
                        _needsSignIn.value = true
                        _needsSignUp.value = false
                    } else {
                        // 최초 설치 → SignUp 화면으로
                        _needsSignUp.value = true
                        _needsSignIn.value = false
                    }
                }
                _isLoadingUser.value = false
                return@launchCatching
            }

            val userProfile = userProfileRepository.getUserProfile(userId)
            _needsOnboarding.value = userProfile?.needsOnboarding ?: true
            _needsSignUp.value = false
            _needsSignIn.value = false

            _isLoadingUser.value = false
        }
    }

    private suspend fun checkForExistingUserData(): Boolean {
        // Phase 1에서는 UserProfile 데이터가 있는지 확인하여 기존 사용자 판단
        // 로그아웃 후에는 UserProfile이 남아있을 수 있으므로 이를 기준으로 판단
        return try {
            // 임시 사용자 ID로 프로필 존재 여부 확인
            // 실제로는 더 정교한 로직이 필요하지만, Phase 1에서는 간단하게 처리
            // 로그아웃 후에는 기존 사용자로 간주하여 SignIn 화면으로 이동
            userProfileRepository.hasUserProfile("temp_user") || 
            userProfileRepository.hasUserProfile("anonymous_user") ||
            // 실제로는 어떤 사용자든 프로필이 있으면 기존 사용자로 간주
            true // Phase 1에서는 로그아웃 후 항상 기존 사용자로 처리
        } catch (e: Exception) {
            // 에러 발생 시에도 기존 사용자로 간주 (SignIn 화면으로)
            true
        }
    }

    fun updateItem(item: TodoItem) {
        launchCatching {
            todoItemRepository.update(item)
        }
    }
}
