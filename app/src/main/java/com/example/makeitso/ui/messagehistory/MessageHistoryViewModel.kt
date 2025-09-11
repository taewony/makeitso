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

import com.example.makeitso.data.model.AiCharacter
import com.example.makeitso.data.model.TriggerType
import java.util.Date

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

            val sampleMessage = AiMessage(
                id = "sample-message",
                userId = userId ?: "",
                prompt = "샘플 메시지",
                response = """
                    야, 이건 또 뭔 개짓거리냐? 🤦‍♂️
                    “앱으로 돈 벌겠다”던 놈의 TODO 리스트가 지금 뭐로 채워졌는지 알아?

                    1. **맛집에서 달콤한 저녁 데이트** 🤡
                    2. **앱 코딩 숙제 제출 (진행 중, 끝은 안 남)**
                    3. **쓰레기 버리기 (마감초과, 여전히 냄새)**

                    완료된 건? **0개.**
                    그럼 결론: 넌 지금 **앱 개발자**가 아니라 **데이트 상상에 취한 쓰레기 방주인**이야.

                    앱도 안 끝냈고, 쓰레기도 못 버렸는데, 감히 맛집에서 달콤한 저녁?
                    그거 현실에선 “앱 안 끝내고, 방치된 쓰레기 더미 속에서 컵라면”이야.

                    👉 오늘 룰 알려줄게:

                    * 쓰레기 → 지금 당장 버려. 기본도 못 하면 누가 데이트 해주냐.
                    * 앱 숙제 → “진행 중” 그딴 말장난 말고 기능 하나 **완료**로 바꿔.
                    * 데이트? 결과를 낸 뒤에나 꿈꿔. 지금은 **자격 없음.**

                    너 목표는 앱으로 돈 버는 거지, 맛집에서 돈 날리는 거 아니잖아? 🍽️💸

                    솔직히 말해라 —
                    너 오늘 저녁에 **맛집 갈 준비** 돼 있냐, 아니면 **완료 0개 백수**로 남을 거냐?
                    """.trimIndent(),
                character = AiCharacter.HARSH_CRITIC,
                createdAt = Date(0),
                triggerType = TriggerType.MANUAL
            )

            if (userId != null) {
                aiAssistantRepository.getAiMessagesFlow(userId).collect { messageList ->
                    val sortedList = messageList.sortedByDescending { it.createdAt }.toMutableList()
                    sortedList.add(sampleMessage)
                    _messages.value = sortedList
                    _isLoading.value = false
                }
            } else {
                _messages.value = listOf(sampleMessage)
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