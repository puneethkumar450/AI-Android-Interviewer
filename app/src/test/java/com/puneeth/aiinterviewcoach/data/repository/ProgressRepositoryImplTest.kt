package com.puneeth.aiinterviewcoach.data.repository

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.puneeth.aiinterviewcoach.domain.model.InterviewCategory
import com.puneeth.aiinterviewcoach.domain.model.InterviewDifficulty
import com.puneeth.aiinterviewcoach.domain.model.InterviewMessage
import com.puneeth.aiinterviewcoach.domain.model.InterviewSession
import com.puneeth.aiinterviewcoach.domain.model.MessageRole
import com.puneeth.aiinterviewcoach.domain.model.SessionFeedback
import com.puneeth.aiinterviewcoach.domain.repository.InterviewRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ProgressRepositoryImplTest {
    @Test
    fun `observe progress summary aggregates completed sessions`() = runTest {
        val sessions = MutableStateFlow(
            listOf(
                sampleSession(
                    category = InterviewCategory.KOTLIN,
                    score = 82,
                    weaknesses = listOf("Testing detail"),
                ),
                sampleSession(
                    category = InterviewCategory.HILT,
                    score = 68,
                    weaknesses = listOf("Testing detail", "Scoping nuance"),
                ),
            ),
        )
        val repository = ProgressRepositoryImpl(
            interviewRepository = object : InterviewRepository {
                override fun observeCompletedSessions(): Flow<List<InterviewSession>> = sessions
                override suspend fun saveInterviewSession(session: InterviewSession) = Unit
            },
        )

        repository.observeProgressSummary().test {
            val item = awaitItem()
            assertThat(item.completedInterviews).isEqualTo(2)
            assertThat(item.averageScore).isEqualTo(75)
            assertThat(item.weakAreas).contains("Testing detail")
        }
    }

    private fun sampleSession(
        category: InterviewCategory,
        score: Int,
        weaknesses: List<String>,
    ) = InterviewSession(
        id = 1,
        category = category,
        difficulty = InterviewDifficulty.MID,
        startedAt = 1,
        completedAt = 2,
        messages = listOf(
            InterviewMessage(
                id = "1",
                role = MessageRole.INTERVIEWER,
                content = "Sample",
                timestamp = 1,
            ),
        ),
        feedback = SessionFeedback(
            score = score,
            strengths = listOf("Communication"),
            weaknesses = weaknesses,
            improvementSuggestions = listOf("More specifics"),
            recommendedTopics = listOf(category.title),
            summary = "Summary",
        ),
    )
}
