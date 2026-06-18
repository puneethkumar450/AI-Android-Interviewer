package com.puneeth.aiinterviewcoach.data.remote

import com.google.common.truth.Truth.assertThat
import com.puneeth.aiinterviewcoach.domain.model.InterviewCategory
import com.puneeth.aiinterviewcoach.domain.model.InterviewDifficulty
import org.junit.Test

class GeminiPromptFactoryTest {
    private val factory = GeminiPromptFactory()

    @Test
    fun `follow up prompt contains structured response contract`() {
        val prompt = factory.followUpPrompt(
            category = InterviewCategory.HILT,
            difficulty = InterviewDifficulty.SENIOR,
            currentQuestion = "How does Hilt scope work?",
            userAnswer = "It creates components for Android classes.",
        )

        assertThat(prompt).contains("FEEDBACK:")
        assertThat(prompt).contains("SCORE:")
        assertThat(prompt).contains("NEXT_QUESTION:")
    }
}
