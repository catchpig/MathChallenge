package com.thinkingplanet.domain.model

data class Question(
    val id: String,
    val type: QuestionType,
    val text: String,
    val imageRes: String?,
    val options: List<String>,
    val answerIndex: Int,
    val hint: String
)

enum class QuestionType { CHOICE }
