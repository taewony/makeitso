package com.example.makeitso.service

import com.example.makeitso.data.model.AiCharacter
import com.example.makeitso.data.model.Deadline
import com.example.makeitso.data.model.TodoItem
import com.example.makeitso.data.model.TriggerType
import com.example.makeitso.data.model.UserGoals
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class AiPromptService @Inject constructor() {

    fun generatePrompt(
        goals: UserGoals,
        todoItems: List<TodoItem>,
        character: AiCharacter,
        triggerType: TriggerType
    ): String {
        return buildStructuredPrompt(goals, todoItems, character, triggerType)
    }

    // Phase 2: 개선된 구조화된 프롬프트 생성
    private fun buildStructuredPrompt(
        goals: UserGoals,
        todoItems: List<TodoItem>,
        character: AiCharacter,
        triggerType: TriggerType
    ): String {
        val incompleteTasks = todoItems.filter { !it.completed }
        val completedTasks = todoItems.filter { it.completed }
        val overdueTasks = getOverdueTasks(todoItems)
        val flaggedTasks = todoItems.filter { it.flagged && !it.completed }
        val highPriorityTasks = incompleteTasks.filter { it.priority >= 2 }
        
        val currentTime = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).format(Date())
        
        return """
=== AI 캐릭터 프롬프트 ===
캐릭터: ${character.displayName}
페르소나: ${character.promptPersona}
생성 시간: $currentTime
트리거: ${if (triggerType == TriggerType.MANUAL) "사용자 수동 호출" else "TODO 생성 자동 트리거"}

=== 사용자 목표 분석 ===
🎯 단기 목표: "${goals.shortTermGoal}"
🏆 장기 목표: "${goals.longTermGoal}"

=== 현재 상황 요약 ===
📊 전체 할 일: ${todoItems.size}개
✅ 완료된 할 일: ${completedTasks.size}개 (${if (todoItems.isNotEmpty()) String.format("%.1f", completedTasks.size * 100.0 / todoItems.size) else "0.0"}%)
⏳ 미완료 할 일: ${incompleteTasks.size}개
🚨 마감 지난 할 일: ${overdueTasks.size}개
🔥 진행 중인 할 일: ${flaggedTasks.size}개
⚡ 높은 우선순위: ${highPriorityTasks.size}개

=== 미완료 할 일 상세 ===
${if (incompleteTasks.isEmpty()) {
    "현재 미완료 할 일이 없습니다."
} else {
    incompleteTasks.mapIndexed { index, item ->
        val status = when {
            item.flagged -> "🔥 진행중"
            isOverdue(item.createdAt, item.deadline, Date()) -> "🚨 마감초과"
            item.priority >= 2 -> "⚡ 높은우선순위"
            else -> "📝 대기중"
        }
        "${index + 1}. ${item.title} [$status] (우선순위: ${getPriorityText(item.priority)}, 마감: ${item.deadline.displayName})"
    }.joinToString("\n")
}}

=== 최근 완료된 할 일 ===
${if (completedTasks.isEmpty()) {
    "최근 완료된 할 일이 없습니다."
} else {
    completedTasks.takeLast(3).mapIndexed { index, item ->
        "✅ ${item.title}"
    }.joinToString("\n")
}}

=== 캐릭터 지침 ===
- ${character.displayName}의 톤앤매너로 응답
- 사용자의 목표와 현재 상황을 고려한 개인화된 조언
- ${if (triggerType == TriggerType.AUTO_CREATE) "새로운 할 일 추가에 대한 격려 또는 조언" else "현재 상황에 대한 동기부여 메시지"}
- 구체적이고 실행 가능한 제안 포함
        """.trimIndent()
    }

    fun generateResponse(
        goals: UserGoals,
        todoItems: List<TodoItem>,
        character: AiCharacter,
        triggerType: TriggerType
    ): String {
        val incompleteTasks = todoItems.filter { !it.completed }
        val overdueTasks = getOverdueTasks(todoItems)
        val completedTasks = todoItems.filter { it.completed }

        return when (character) {
            AiCharacter.HARSH_CRITIC -> generateHarshCriticResponse(
                incompleteTasks.size, overdueTasks.size, completedTasks.size, triggerType, goals
            )
            AiCharacter.NAGGING_GIRLFRIEND -> generateNaggingGirlfriendResponse(
                incompleteTasks.size, overdueTasks.size, completedTasks.size, triggerType, goals
            )
            AiCharacter.COLD_PRINCESS -> generateColdPrincessResponse(
                incompleteTasks.size, overdueTasks.size, completedTasks.size, triggerType, goals
            )
        }
    }

    private fun generateHarshCriticResponse(
        incompleteTasks: Int,
        overdueTasks: Int,
        completedTasks: Int,
        triggerType: TriggerType,
        goals: UserGoals
    ): String {
        val responses = when {
            overdueTasks > 0 -> listOf(
                "야, 마감 지난 일이 ${overdueTasks}개나 있는데 뭐하고 있어? 시간은 기다려주지 않는다고!",
                "마감 넘긴 일들 보니까 한심하다. ${goals.shortTermGoal} 이루려면 정신 차려야지.",
                "이런 식으로 하다간 ${goals.longTermGoal}는 꿈도 못 꿔. 당장 움직여!"
            )
            incompleteTasks > 5 -> listOf(
                "할 일이 ${incompleteTasks}개나 쌓여있네? 미루기의 달인이구나.",
                "이렇게 쌓아두고 언제 다 할 거야? ${goals.shortTermGoal} 포기할 거면 말하고.",
                "할 일 목록만 길어지고 실행은 안 하면 뭐해? 행동으로 보여줘!"
            )
            completedTasks == 0 -> listOf(
                "아직 완료한 게 하나도 없네? 시작이 반이라더니 시작도 안 했잖아.",
                "계획만 세우고 실행 안 하면 그냥 꿈일 뿐이야. 당장 하나라도 해!",
                "이런 식이면 ${goals.longTermGoal} 달성은 다음 생에나 가능할 듯."
            )
            else -> listOf(
                "그래도 ${completedTasks}개는 했네. 하지만 아직 ${incompleteTasks}개 남았어.",
                "조금씩 하고 있긴 하지만 속도가 너무 느려. 더 빨리 움직여!",
                "완료한 것보다 남은 게 더 많잖아. 집중해서 끝내버려!"
            )
        }
        
        return responses[Random.nextInt(responses.size)]
    }

    private fun generateNaggingGirlfriendResponse(
        incompleteTasks: Int,
        overdueTasks: Int,
        completedTasks: Int,
        triggerType: TriggerType,
        goals: UserGoals
    ): String {
        val responses = when {
            overdueTasks > 0 -> listOf(
                "오빠~ 마감 지난 일이 ${overdueTasks}개나 있어요! 이러면 안 되는 거 알죠?",
                "진짜 답답해 죽겠어요! ${goals.shortTermGoal} 하려면 시간 관리부터 해야죠!",
                "오빠가 이런 식으로 하면 제가 얼마나 걱정되는지 알아요?"
            )
            incompleteTasks > 5 -> listOf(
                "할 일이 ${incompleteTasks}개나 쌓였어요! 오빠, 이거 언제 다 할 거예요?",
                "미루고 미루다가 결국 못 하게 되는 거 아시죠? ${goals.longTermGoal} 포기할 거예요?",
                "제가 몇 번을 말했는데 왜 안 들어요? 정말 속상해요!"
            )
            completedTasks == 0 -> listOf(
                "오빠~ 아직 하나도 안 했어요? 이러면 정말 안 되는데...",
                "계획만 세우고 실행 안 하면 어떡해요! 제가 도와드릴까요?",
                "오빠 이런 모습 보기 싫어요. 제발 하나라도 시작해 주세요!"
            )
            else -> listOf(
                "오빠 ${completedTasks}개 했네요! 그래도 아직 ${incompleteTasks}개 더 있어요~",
                "조금씩 하고 있긴 하지만 더 열심히 해야죠! 오빠 할 수 있어요!",
                "이 정도면 괜찮긴 한데... 더 빨리 끝내면 좋겠어요!"
            )
        }
        
        return responses[Random.nextInt(responses.size)]
    }

    private fun generateColdPrincessResponse(
        incompleteTasks: Int,
        overdueTasks: Int,
        completedTasks: Int,
        triggerType: TriggerType,
        goals: UserGoals
    ): String {
        val responses = when {
            overdueTasks > 0 -> listOf(
                "마감을 넘긴 일이 ${overdueTasks}개... 시간 관리 능력이 부족하군요.",
                "${goals.shortTermGoal}를 달성하려면 이런 식으로는 불가능합니다.",
                "약속을 지키지 못하는 사람에게 성공은 없어요."
            )
            incompleteTasks > 5 -> listOf(
                "할 일이 ${incompleteTasks}개나 쌓여있네요. 계획성이 없군요.",
                "이런 식으로 미루다가는 ${goals.longTermGoal}는 영원히 달성 못 할 겁니다.",
                "효율성이 떨어지는 사람은 성공할 자격이 없어요."
            )
            completedTasks == 0 -> listOf(
                "아직 완료한 게 하나도 없다니... 실망스럽군요.",
                "행동하지 않으면 아무것도 이룰 수 없어요. 당연한 얘기죠.",
                "이런 태도로는 목표 달성은 꿈도 꾸지 마세요."
            )
            else -> listOf(
                "${completedTasks}개 완료... 그럭저럭이군요. 하지만 아직 ${incompleteTasks}개 남았어요.",
                "진전이 있긴 하지만 속도가 느려요. 더 효율적으로 하세요.",
                "이 정도 수준으로는 평범한 결과밖에 얻을 수 없을 겁니다."
            )
        }
        
        return responses[Random.nextInt(responses.size)]
    }

    private fun getOverdueTasks(todoItems: List<TodoItem>): List<TodoItem> {
        val now = Date()
        return todoItems.filter { item ->
            !item.completed && item.deadline != Deadline.NONE && 
            isOverdue(item.createdAt, item.deadline, now)
        }
    }

    private fun isOverdue(createdAt: Date, deadline: Deadline, now: Date): Boolean {
        val deadlineMillis = createdAt.time + TimeUnit.HOURS.toMillis(deadline.hours.toLong())
        return now.time > deadlineMillis
    }

    private fun getPriorityText(priority: Int): String {
        return when (priority) {
            0 -> "없음"
            1 -> "낮음"
            2 -> "보통"
            3 -> "높음"
            else -> "알 수 없음"
        }
    }
}