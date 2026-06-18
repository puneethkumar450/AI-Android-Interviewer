package com.puneeth.aiinterviewcoach.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puneeth.aiinterviewcoach.domain.model.PracticeQuestion
import com.puneeth.aiinterviewcoach.domain.usecase.ObserveQuestionBankUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class SearchUiState(
    val query: String = "",
    val questions: List<PracticeQuestion> = emptyList(),
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val observeQuestions: ObserveQuestionBankUseCase,
) : ViewModel() {
    private val query = MutableStateFlow("")

    val uiState: StateFlow<SearchUiState> = query.flatMapLatest { search ->
        observeQuestions(
            searchQuery = search,
            category = null,
            difficulty = null,
            bookmarksOnly = false,
        ).map { SearchUiState(query = search, questions = it) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SearchUiState())

    fun updateQuery(value: String) {
        query.value = value
    }
}
