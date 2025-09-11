package com.example.makeitso.ui.todoitem

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.example.makeitso.MainViewModel
import com.example.makeitso.R
import com.example.makeitso.data.model.ErrorMessage
import com.example.makeitso.data.model.TodoItem
import com.example.makeitso.data.model.TriggerType
import com.example.makeitso.data.repository.AiAssistantRepository
import com.example.makeitso.data.repository.AuthRepository
import com.example.makeitso.data.repository.TodoItemRepository
import com.example.makeitso.data.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import android.util.Log

@HiltViewModel
class TodoItemViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val authRepository: AuthRepository,
    private val todoItemRepository: TodoItemRepository,
    private val userProfileRepository: UserProfileRepository,
    private val aiAssistantRepository: AiAssistantRepository
) : MainViewModel() {
    private val _navigateToTodoList = MutableStateFlow(false)
    val navigateToTodoList: StateFlow<Boolean>
        get() = _navigateToTodoList.asStateFlow()

    private val todoItemRoute = savedStateHandle.toRoute<TodoItemRoute>()
    private val itemId: String = todoItemRoute.itemId

    private val _todoItem = MutableStateFlow<TodoItem?>(null)
    val todoItem: StateFlow<TodoItem?>
        get() = _todoItem.asStateFlow()

    private val _showAutoAiNudge = MutableStateFlow(false)
    val showAutoAiNudge: StateFlow<Boolean>
        get() = _showAutoAiNudge.asStateFlow()

    private val _autoAiNudgeMessage = MutableStateFlow("")
    val autoAiNudgeMessage: StateFlow<String>
        get() = _autoAiNudgeMessage.asStateFlow()

    private val _showAutoPromptDialog = MutableStateFlow(false)
    val showAutoPromptDialog: StateFlow<Boolean>
        get() = _showAutoPromptDialog.asStateFlow()

    private val _currentAutoPrompt = MutableStateFlow("")
    val currentAutoPrompt: StateFlow<String>
        get() = _currentAutoPrompt.asStateFlow()

    fun loadItem() {
        Log.d("TodoItemViewModel", "loadItem called with itemId: $itemId")
        launchCatching {
            if (itemId.isBlank()) {
                _todoItem.value = TodoItem()
                Log.d("TodoItemViewModel", "New TodoItem created: ${_todoItem.value}")
            } else {
                _todoItem.value = todoItemRepository.getTodoItem(itemId)
                Log.d("TodoItemViewModel", "Loaded TodoItem: ${_todoItem.value}")
            }
        }
    }

    fun saveItem(
        item: TodoItem,
        showErrorSnackbar: (ErrorMessage) -> Unit
    ) {
        val ownerId = authRepository.currentUserIdFlow.value
        Log.d("TodoItemViewModel", "saveItem called with ownerId: $ownerId, item: $item")

        if (ownerId.isNullOrBlank()) {
            showErrorSnackbar(ErrorMessage.IdError(R.string.could_not_find_account))
            Log.e("TodoItemViewModel", "Owner ID is null or blank.")
            return
        }

        if (item.title.isBlank()) {
            showErrorSnackbar(ErrorMessage.IdError(R.string.item_without_title))
            Log.e("TodoItemViewModel", "Item title is blank.")
            return
        }

        launchCatching {
            val isNewItem = itemId.isBlank()
            
            if (isNewItem) {
                val newId = todoItemRepository.create(item.copy(ownerId = ownerId))
                Log.d("TodoItemViewModel", "Created new TodoItem with ID: $newId")
                
                // 새 TODO 생성 시 자동 AI Nudge 트리거
                triggerAutoAiNudge(ownerId)
                
                // AI Nudge가 표시되는 경우 화면 전환을 지연
                // 다이얼로그가 닫힐 때까지 기다림
                return@launchCatching
            } else {
                todoItemRepository.update(item)
                Log.d("TodoItemViewModel", "Updated TodoItem: $item")
                
                _navigateToTodoList.value = true
                Log.d("TodoItemViewModel", "Navigation to TodoList triggered.")
            }
        }
    }

    fun deleteItem(item: TodoItem) {
        Log.d("TodoItemViewModel", "deleteItem called with item: $item")
        launchCatching {
            if (itemId.isNotBlank()) {
                todoItemRepository.delete(item.id)
                Log.d("TodoItemViewModel", "Deleted TodoItem with ID: ${item.id}")
            }
            _navigateToTodoList.value = true
            Log.d("TodoItemViewModel", "Navigation to TodoList triggered after deletion.")
        }
    }

    private fun triggerAutoAiNudge(userId: String) {
        launchCatching {
            Log.d("TodoItemViewModel", "Triggering auto AI nudge for user: $userId")
            
            val userProfile = userProfileRepository.getUserProfile(userId)
            if (userProfile == null) {
                Log.w("TodoItemViewModel", "User profile not found for auto AI nudge")
                return@launchCatching
            }

            // 현재 TODO 아이템들을 가져와서 AI 응답 생성
            val currentTodoItems = todoItemRepository.getAllTodoItems(userId)
            
            val aiMessage = aiAssistantRepository.generateAiResponse(
                userId = userId,
                goals = userProfile.goals,
                todoItems = currentTodoItems,
                character = userProfile.selectedCharacter,
                triggerType = TriggerType.AUTO_CREATE
            )
            
            _autoAiNudgeMessage.value = aiMessage.response
            _currentAutoPrompt.value = aiMessage.prompt
            _showAutoAiNudge.value = true
            Log.d("TodoItemViewModel", "Auto AI nudge generated: ${aiMessage.response.take(50)}...")
        }
    }

    fun dismissAutoAiNudge() {
        _showAutoAiNudge.value = false
        _autoAiNudgeMessage.value = ""
        _currentAutoPrompt.value = ""
        
        // AI Nudge 다이얼로그가 닫힌 후 화면 전환
        _navigateToTodoList.value = true
        Log.d("TodoItemViewModel", "Navigation to TodoList triggered after AI nudge dismissal.")
    }

    fun showAutoPrompt() {
        _showAutoPromptDialog.value = true
    }

    fun hideAutoPrompt() {
        _showAutoPromptDialog.value = false
        
        // 프롬프트 다이얼로그가 닫힌 후 화면 전환
        _navigateToTodoList.value = true
        Log.d("TodoItemViewModel", "Navigation to TodoList triggered after prompt dialog dismissal.")
    }
}
