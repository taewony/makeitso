package com.example.makeitso.data.model

data class UserGoals(
    val shortTermGoal: String = "",
    val longTermGoal: String = ""
)

val UserGoals.isEmpty: Boolean
    get() = shortTermGoal.isBlank() && longTermGoal.isBlank()

val UserGoals.isComplete: Boolean
    get() = shortTermGoal.isNotBlank() && longTermGoal.isNotBlank()