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

    private val _showPromptDialog = MutableStateFlow(false)
    val showPromptDialog: StateFlow<Boolean>
        get() = _showPromptDialog.asStateFlow()

    private val _currentPrompt = MutableStateFlow("")
    val currentPrompt: StateFlow<String>
        get() = _currentPrompt.asStateFlow()

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
            _currentPrompt.value = aiMessage.prompt
            _showAiNudgeDialog.value = true
        }
    }

    fun onDialogDismiss() {
        _showAiNudgeDialog.value = false
        _aiNudgeMessage.value = ""
        _currentPrompt.value = ""
    }

    fun showPrompt() {
        _showPromptDialog.value = true
    }

    fun hidePrompt() {
        _showPromptDialog.value = false
    }

    fun loadCurrentUser() {
        // 로컬 DB 데이터 출력을 별도 코루틴에서 실행 (메인 스레드 블로킹 방지)
        launchCatching {
            printLocalDbData()
        }
        
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

    private suspend fun printLocalDbData() {
        try {
            android.util.Log.d("TodoListViewModel", "=== 로컬 DB 데이터 출력 시작 ===")
            
            // 현재 로그인된 사용자 이메일 출력
            val currentUserEmail = authRepository.getCurrentUserEmail()
            android.util.Log.d("TodoListViewModel", "현재 로그인된 사용자 이메일: $currentUserEmail")
            
            // 모든 사용자 프로필 출력 (실제로는 현재 사용자만)
            val currentUserId = authRepository.getCurrentUserId()
            if (currentUserId != null) {
                val userProfile = userProfileRepository.getUserProfile(currentUserId)
                android.util.Log.d("TodoListViewModel", "사용자 프로필: $userProfile")
                
                // TODO 아이템들 출력
                val todoItems = todoItemRepository.getAllTodoItems(currentUserId)
                android.util.Log.d("TodoListViewModel", "TODO 아이템 개수: ${todoItems.size}")
                todoItems.take(5).forEachIndexed { index, item -> // 최대 5개만 출력
                    android.util.Log.d("TodoListViewModel", "TODO[$index]: ${item.title} (완료: ${item.completed})")
                }
                
                // AI 메시지 개수만 간단히 출력 (Flow collect 제거)
                android.util.Log.d("TodoListViewModel", "AI 메시지 조회 생략 (메인 스레드 보호)")
            } else {
                android.util.Log.d("TodoListViewModel", "현재 로그인된 사용자가 없습니다.")
            }
            
            android.util.Log.d("TodoListViewModel", "=== 로컬 DB 데이터 출력 완료 ===")
        } catch (e: Exception) {
            android.util.Log.e("TodoListViewModel", "로컬 DB 데이터 출력 중 오류: ${e.message}", e)
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
