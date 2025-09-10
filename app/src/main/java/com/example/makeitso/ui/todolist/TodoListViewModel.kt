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
            val currentTodoItems = todoItemRepository.getTodoItems(authRepository.currentUserIdFlow)
            
            // 현재 TODO 아이템들을 가져와서 AI 응답 생성
            // 임시로 빈 리스트 사용 (실제로는 Flow에서 현재 값을 가져와야 함)
            val aiMessage = aiAssistantRepository.generateAiResponse(
                userId = userId,
                goals = userProfile.goals,
                todoItems = emptyList(), // TODO: 실제 todoItems 값 사용
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
        // Phase 1에서는 간단한 로직: SharedPreferences에 사용자 데이터가 있는지 확인
        // 실제로는 더 정교한 로직이 필요할 수 있음
        return false // 현재는 항상 새 사용자로 처리
    }

    fun updateItem(item: TodoItem) {
        launchCatching {
            todoItemRepository.update(item)
        }
    }
}
