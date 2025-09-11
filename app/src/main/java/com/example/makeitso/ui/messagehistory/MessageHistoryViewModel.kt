package com.example.makeitso.ui.messagehistory

import com.example.makeitso.MainViewModel
import com.example.makeitso.data.model.AiMessage
import com.example.makeitso.data.repository.AiAssistantRepository
import com.example.makeitso.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MessageHistoryViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val aiAssistantRepository: AiAssistantRepository
) : MainViewModel() {
    
    private val _messages = MutableStateFlow<List<AiMessage>>(emptyList())
    val messages: StateFlow<List<AiMessage>> = _messages.asStateFlow()
    
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadMessages() {
        launchCatching {
            _isLoading.value = true
            val userId = authRepository.getCurrentUserId()
            
            if (userId != null) {
                aiAssistantRepository.getAiMessagesFlow(userId).collect { messageList ->
                    // 최신 메시지부터 표시 (시간 역순)
                    _messages.value = messageList.sortedByDescending { it.createdAt }
                    _isLoading.value = false
                }
            } else {
                _messages.value = emptyList()
                _isLoading.value = false
            }
        }
    }

    fun deleteAllMessages() {
        launchCatching {
            val userId = authRepository.getCurrentUserId()
            if (userId != null) {
                aiAssistantRepository.clearAiMessages(userId)
            }
        }
    }
}