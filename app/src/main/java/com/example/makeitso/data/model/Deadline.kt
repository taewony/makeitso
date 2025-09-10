package com.example.makeitso.data.model

enum class Deadline(val displayName: String, val hours: Int) {
    NONE("설정 안함", 0),
    WITHIN_24H("24시간 내", 24),
    WITHIN_WEEK("1주일 내", 168)
}