package com.puneeth.aiinterviewcoach.data.repository

import com.puneeth.aiinterviewcoach.data.remote.GeminiPromptFactory
import com.puneeth.aiinterviewcoach.data.remote.GeminiRemoteDataSource
import com.puneeth.aiinterviewcoach.domain.model.InterviewCategory
import com.puneeth.aiinterviewcoach.domain.model.InterviewDifficulty
import com.puneeth.aiinterviewcoach.domain.model.MockInterviewTurn
import com.puneeth.aiinterviewcoach.domain.model.SessionFeedback
import com.puneeth.aiinterviewcoach.domain.repository.AiInterviewRepository
import javax.inject.Inject

class AiInterviewRepositoryImpl @Inject constructor(
    private val promptFactory: GeminiPromptFactory,
    private val remoteDataSource: GeminiRemoteDataSource,
) : AiInterviewRepository {
    override suspend fun startInterview(
        category: InterviewCategory,
        difficulty: InterviewDifficulty,
    ): String {
        val prompt = promptFactory.startPrompt(category, difficulty)
        return remoteDataSource.generateText(prompt).ifBlank {
            "Tell me about a time you used ${category.title} in an Android project, and explain the trade-offs behind your implementation."
        }
    }

    override suspend fun continueInterview(
        category: InterviewCategory,
        difficulty: InterviewDifficulty,
        currentQuestion: String,
        userAnswer: String,
    ): MockInterviewTurn {
        val prompt = promptFactory.followUpPrompt(category, difficulty, currentQuestion, userAnswer)
        val raw = remoteDataSource.generateText(prompt)
        return raw.toMockTurnFallback(category, currentQuestion)
    }

    override suspend fun evaluateInterview(
        transcript: String,
        category: InterviewCategory,
        difficulty: InterviewDifficulty,
    ): SessionFeedback {
        val prompt = promptFactory.evaluationPrompt(transcript, category, difficulty)
        val raw = remoteDataSource.generateText(prompt)
        return raw.toSessionFeedbackFallback(category)
    }

    private fun String.toMockTurnFallback(
        category: InterviewCategory,
        currentQuestion: String,
    ): MockInterviewTurn {
        if (isBlank()) {
            return MockInterviewTurn(
                candidateAnswer = "",
                nextQuestion = "What would you improve in your previous ${category.title} answer if this were a real production incident?",
                quickFeedback = "Clear foundation. Add more production trade-offs, testing detail, and lifecycle awareness.",
                provisionalScore = 76,
            )
        }
        val feedback = lineStartingWith("FEEDBACK:")
        val score = lineStartingWith("SCORE:").filter { it.isDigit() }.toIntOrNull() ?: 78
        val nextQuestion = lineStartingWith("NEXT_QUESTION:").ifBlank {
            "Can you go deeper on the trade-offs involved in: $currentQuestion"
        }
        return MockInterviewTurn(
            candidateAnswer = "",
            nextQuestion = nextQuestion,
            quickFeedback = feedback.ifBlank { "Solid answer. Add more specifics and examples." },
            provisionalScore = score,
        )
    }

    private fun String.toSessionFeedbackFallback(category: InterviewCategory): SessionFeedback {
        if (isBlank()) {
            return SessionFeedback(
                score = 80,
                strengths = listOf("Strong Android fundamentals", "Good communication", "Reasonable trade-off awareness"),
                weaknesses = listOf("Could deepen architecture examples", "Needs more testing detail", "Could quantify outcomes better"),
                improvementSuggestions = listOf("Use STAR examples", "Mention failure handling", "Connect design choices to scale"),
                recommendedTopics = listOf(category.title, "Testing strategy", "Performance and observability"),
                summary = "You demonstrated practical Android understanding with room to improve depth, specificity, and production framing.",
            )
        }
        return SessionFeedback(
            score = lineStartingWith("SCORE:").filter { it.isDigit() }.toIntOrNull() ?: 80,
            strengths = pipeValues("STRENGTHS:"),
            weaknesses = pipeValues("WEAKNESSES:"),
            improvementSuggestions = pipeValues("IMPROVEMENTS:"),
            recommendedTopics = pipeValues("TOPICS:"),
            summary = lineStartingWith("SUMMARY:"),
        )
    }

    private fun String.lineStartingWith(prefix: String): String {
        return lines().firstOrNull { it.startsWith(prefix) }?.removePrefix(prefix)?.trim().orEmpty()
    }

    private fun String.pipeValues(prefix: String): List<String> {
        return lineStartingWith(prefix).split("|").map { it.trim() }.filter { it.isNotBlank() }
    }
}
