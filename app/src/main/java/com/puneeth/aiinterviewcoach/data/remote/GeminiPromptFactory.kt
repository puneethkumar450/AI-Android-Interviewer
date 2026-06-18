package com.puneeth.aiinterviewcoach.data.remote

import com.puneeth.aiinterviewcoach.domain.model.InterviewCategory
import com.puneeth.aiinterviewcoach.domain.model.InterviewDifficulty
import javax.inject.Inject

class GeminiPromptFactory @Inject constructor() {
    fun startPrompt(category: InterviewCategory, difficulty: InterviewDifficulty): String = """
        You are a senior Android interviewer.
        Start a mock interview for the category ${category.title} at ${difficulty.title} level.
        Ask one interview question only.
        Keep it realistic, concise, and targeted for Android engineers.
    """.trimIndent()

    fun followUpPrompt(
        category: InterviewCategory,
        difficulty: InterviewDifficulty,
        currentQuestion: String,
        userAnswer: String,
    ): String = """
        You are conducting an Android mock interview.
        Category: ${category.title}
        Difficulty: ${difficulty.title}
        Current question: $currentQuestion
        Candidate answer: $userAnswer

        Return exactly this format:
        FEEDBACK: one concise paragraph
        SCORE: integer between 0 and 100
        NEXT_QUESTION: one follow-up interview question based on the candidate answer
    """.trimIndent()

    fun evaluationPrompt(
        transcript: String,
        category: InterviewCategory,
        difficulty: InterviewDifficulty,
    ): String = """
        Evaluate this Android interview transcript.
        Category: ${category.title}
        Difficulty: ${difficulty.title}
        Transcript:
        $transcript

        Return exactly this structure:
        SCORE: integer 0-100
        STRENGTHS: point 1 | point 2 | point 3
        WEAKNESSES: point 1 | point 2 | point 3
        IMPROVEMENTS: point 1 | point 2 | point 3
        TOPICS: point 1 | point 2 | point 3
        SUMMARY: one paragraph
    """.trimIndent()
}
