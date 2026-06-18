package com.puneeth.aiinterviewcoach.data.remote

import com.google.common.truth.Truth.assertThat
import com.puneeth.aiinterviewcoach.domain.model.InterviewCategory
import com.puneeth.aiinterviewcoach.domain.model.InterviewDifficulty
import org.junit.Test

class GeminiPromptFactoryTest {
    @Test
    fun `enum parsing supports display titles`() {
        assertThat(InterviewCategory.fromTitle("Jetpack Compose")).isEqualTo(InterviewCategory.JETPACK_COMPOSE)
        assertThat(InterviewDifficulty.fromTitle("Intermediate")).isEqualTo(InterviewDifficulty.INTERMEDIATE)
    }
}
