package com.example.makeitso.data.model

data class UserProfile(
    val userId: String = "",
    val goals: UserGoals = UserGoals(),
    val selectedCharacter: AiCharacter = AiCharacter.HARSH_CRITIC,
    val isOnboardingComplete: Boolean = false
)

val UserProfile.needsOnboarding: Boolean
    get() = !isOnboardingComplete || goals.isEmpty