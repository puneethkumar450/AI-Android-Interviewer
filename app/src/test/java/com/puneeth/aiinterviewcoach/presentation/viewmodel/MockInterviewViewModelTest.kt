package com.puneeth.aiinterviewcoach.presentation.viewmodel

import com.google.common.truth.Truth.assertThat
import com.puneeth.aiinterviewcoach.domain.model.InterviewCategory
import com.puneeth.aiinterviewcoach.domain.model.InterviewDifficulty
import com.puneeth.aiinterviewcoach.domain.model.MockInterviewTurn
import com.puneeth.aiinterviewcoach.domain.model.SessionFeedback
import com.puneeth.aiinterviewcoach.domain.repository.AiInterviewRepository
import com.puneeth.aiinterviewcoach.domain.repository.InterviewRepository
import com.puneeth.aiinterviewcoach.domain.usecase.SaveInterviewResultUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class MockInterviewViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `start interview posts interviewer message`() = runTest {
        val viewModel = MockInterviewViewModel(
            aiInterviewRepository = object : AiInterviewRepository {
                override suspend fun startInterview(
                    category: InterviewCategory,
                    difficulty: InterviewDifficulty,
                ): String = "What is structured concurrency?"

                override suspend fun continueInterview(
                    category: InterviewCategory,
                    difficulty: InterviewDifficulty,
                    currentQuestion: String,
                    userAnswer: String,
                ): MockInterviewTurn = MockInterviewTurn("", "Follow-up?", "Good start", 80)

                override suspend fun evaluateInterview(
                    transcript: String,
                    category: InterviewCategory,
                    difficulty: InterviewDifficulty,
                ): SessionFeedback = SessionFeedback(80, emptyList(), emptyList(), emptyList(), emptyList(), "Summary")
            },
            saveInterviewResult = SaveInterviewResultUseCase(
                interviewRepository = object : InterviewRepository {
                    override fun observeCompletedSessions(): Flow<List<com.puneeth.aiinterviewcoach.domain.model.InterviewSession>> = emptyFlow()
                    override suspend fun saveInterviewSession(session: com.puneeth.aiinterviewcoach.domain.model.InterviewSession) = Unit
                },
            ),
        )

        viewModel.startInterview()
        advanceUntilIdle()

        assertThat(viewModel.uiState.value.messages).hasSize(1)
        assertThat(viewModel.uiState.value.currentQuestion).contains("structured concurrency")
    }
}
