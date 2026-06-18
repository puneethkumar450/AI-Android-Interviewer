package com.puneeth.aiinterviewcoach.domain.usecase

import com.puneeth.aiinterviewcoach.domain.model.InterviewCategory
import com.puneeth.aiinterviewcoach.domain.model.InterviewDifficulty
import com.puneeth.aiinterviewcoach.domain.model.QuestionSession
import com.puneeth.aiinterviewcoach.domain.repository.QuestionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ObserveQuestionSessionUseCase @Inject constructor(
    private val repository: QuestionRepository,
) {
    operator fun invoke(
        startQuestionId: Long?,
        category: InterviewCategory?,
        difficulty: InterviewDifficulty?,
        searchQuery: String,
        bookmarksOnly: Boolean,
    ): Flow<QuestionSession> = flow {
        val ids = repository.getQuestionIds(
            category = category,
            difficulty = difficulty,
            searchQuery = searchQuery,
            bookmarksOnly = bookmarksOnly,
        )
        val currentIndex = when {
            ids.isEmpty() -> -1
            startQuestionId == null -> 0
            else -> ids.indexOf(startQuestionId).takeIf { it >= 0 } ?: 0
        }
        val question = ids.getOrNull(currentIndex)?.let { repository.getQuestion(it) }
        emit(
            QuestionSession(
                questionIds = ids,
                currentIndex = currentIndex,
                question = question,
            ),
        )
    }
}
