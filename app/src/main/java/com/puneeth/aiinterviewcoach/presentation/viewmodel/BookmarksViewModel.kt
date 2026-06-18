package com.puneeth.aiinterviewcoach.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puneeth.aiinterviewcoach.domain.model.PracticeQuestion
import com.puneeth.aiinterviewcoach.domain.repository.QuestionRepository
import com.puneeth.aiinterviewcoach.domain.usecase.ObserveQuestionBankUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class BookmarksUiState(
    val questions: List<PracticeQuestion> = emptyList(),
)

@HiltViewModel
class BookmarksViewModel @Inject constructor(
    private val observeQuestions: ObserveQuestionBankUseCase,
    private val repository: QuestionRepository,
) : ViewModel() {
    val uiState: StateFlow<BookmarksUiState> = observeQuestions(
        searchQuery = "",
        category = null,
        difficulty = null,
        bookmarksOnly = true,
    ).map { BookmarksUiState(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), BookmarksUiState())

    fun removeBookmark(questionId: Long) {
        viewModelScope.launch {
            repository.toggleBookmark(questionId)
        }
    }
}
