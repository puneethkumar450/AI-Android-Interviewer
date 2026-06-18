package com.puneeth.aiinterviewcoach.domain.model

data class QuestionSession(
    val questionIds: List<Long>,
    val currentIndex: Int,
    val question: PracticeQuestion?,
) {
    val hasPrevious: Boolean
        get() = currentIndex > 0

    val hasNext: Boolean
        get() = currentIndex >= 0 && currentIndex < questionIds.lastIndex
}
