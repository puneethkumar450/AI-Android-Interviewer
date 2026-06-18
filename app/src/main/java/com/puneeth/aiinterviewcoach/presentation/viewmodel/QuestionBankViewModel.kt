package com.puneeth.aiinterviewcoach.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puneeth.aiinterviewcoach.domain.model.InterviewCategory
import com.puneeth.aiinterviewcoach.domain.model.InterviewDifficulty
import com.puneeth.aiinterviewcoach.domain.model.PracticeQuestion
import com.puneeth.aiinterviewcoach.domain.repository.QuestionRepository
import com.puneeth.aiinterviewcoach.domain.usecase.ObserveQuestionBankUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class QuestionBankUiState(
    val searchQuery: String = "",
    val selectedCategory: InterviewCategory? = null,
    val selectedDifficulty: InterviewDifficulty? = null,
    val bookmarksOnly: Boolean = false,
    val questions: List<PracticeQuestion> = emptyList(),
)

@HiltViewModel
class QuestionBankViewModel @Inject constructor(
    private val observeQuestionBank: ObserveQuestionBankUseCase,
    private val repository: QuestionRepository,
) : ViewModel() {
    private val searchQuery = MutableStateFlow("")
    private val selectedCategory = MutableStateFlow<InterviewCategory?>(null)
    private val selectedDifficulty = MutableStateFlow<InterviewDifficulty?>(null)
    private val bookmarksOnly = MutableStateFlow(false)

    val screenState: StateFlow<QuestionBankUiState> = combine(
        searchQuery,
        selectedCategory,
        selectedDifficulty,
        bookmarksOnly,
    ) { search, category, difficulty, bookmarks ->
        FilterState(search, category, difficulty, bookmarks)
    }.flatMapLatest { filter ->
        observeQuestionBank(
            searchQuery = filter.search,
            category = filter.category,
            difficulty = filter.difficulty,
            bookmarksOnly = filter.bookmarksOnly,
        ).map { questions ->
            QuestionBankUiState(
                searchQuery = filter.search,
                selectedCategory = filter.category,
                selectedDifficulty = filter.difficulty,
                bookmarksOnly = filter.bookmarksOnly,
                questions = questions,
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), QuestionBankUiState())

    init {
        viewModelScope.launch {
            repository.seedIfNeeded()
        }
    }

    fun updateSearch(query: String) {
        searchQuery.value = query
    }

    fun toggleBookmarksOnly() {
        bookmarksOnly.value = !bookmarksOnly.value
    }

    fun selectCategory(category: InterviewCategory?) {
        selectedCategory.value = category
    }

    fun selectDifficulty(difficulty: InterviewDifficulty?) {
        selectedDifficulty.value = difficulty
    }

    fun toggleBookmark(questionId: Long) {
        viewModelScope.launch {
            repository.toggleBookmark(questionId)
        }
    }

    private data class FilterState(
        val search: String,
        val category: InterviewCategory?,
        val difficulty: InterviewDifficulty?,
        val bookmarksOnly: Boolean,
    )
}
