package com.example.makeitso.data.model

import java.util.Date

data class AiMessage(
    val id: String = "",
    val userId: String = "",
    val prompt: String = "",
    val response: String = "",
    val character: AiCharacter = AiCharacter.HARSH_CRITIC,
    val createdAt: Date = Date(),
    val triggerType: TriggerType = TriggerType.MANUAL
)

enum class TriggerType {
    MANUAL,      // 사용자가 버튼 클릭
    AUTO_CREATE  // TODO 생성 시 자동
}