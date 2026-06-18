package com.puneeth.aiinterviewcoach.presentation.viewmodel

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MockInterviewViewModelTest {
    @Test
    fun `questions ui state exposes navigation affordances`() {
        val state = QuestionsUiState(
            questionIds = listOf(1L, 2L, 3L),
            currentIndex = 1,
        )
        assertThat(state.hasPrevious).isTrue()
        assertThat(state.hasNext).isTrue()
    }
}
