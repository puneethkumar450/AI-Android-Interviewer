package com.puneeth.aiinterviewcoach.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puneeth.aiinterviewcoach.domain.model.CategoryProgress
import com.puneeth.aiinterviewcoach.domain.model.CategorySummary
import com.puneeth.aiinterviewcoach.domain.model.HomeDifficultySummary
import com.puneeth.aiinterviewcoach.domain.model.HomeSummary
import com.puneeth.aiinterviewcoach.domain.model.InterviewCategory
import com.puneeth.aiinterviewcoach.domain.model.InterviewDifficulty
import com.puneeth.aiinterviewcoach.domain.model.RecentActivity
import com.puneeth.aiinterviewcoach.domain.repository.ProgressRepository
import com.puneeth.aiinterviewcoach.domain.repository.QuestionRepository
import com.puneeth.aiinterviewcoach.domain.usecase.ObserveHomeSummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(
    val summary: HomeSummary = HomeSummary(
        totalQuestions = 0,
        bookmarksCount = 0,
        continueQuestionId = null,
        categories = InterviewCategory.entries.map { CategorySummary(it, 0) },
        difficultySummaries = InterviewDifficulty.entries.map { HomeDifficultySummary(it, 0) },
    ),
    val streak: Int = 0,
    val completedQuestions: Int = 0,
    val lastPracticeEpochDay: Long = 0L,
    val weakCategories: List<CategoryProgress> = emptyList(),
    val recentActivity: RecentActivity? = null,
    val unviewedCount: Int = 0,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    observeHomeSummary: ObserveHomeSummaryUseCase,
    private val progressRepository: ProgressRepository,
    private val questionRepository: QuestionRepository,
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        observeHomeSummary(),
        progressRepository.observeProgressSummary(),
        progressRepository.observeRecentActivity(),
        progressRepository.observeUnviewedCount(),
    ) { summary, progress, recentActivity, unviewedCount ->
        HomeUiState(
            summary = summary,
            streak = progress.currentStreak,
            completedQuestions = progress.completedQuestions,
            weakCategories = progress.categoryProgress
                .filter { it.totalCount > 0 }
                .sortedBy { it.completionPercent }
                .take(3),
            recentActivity = recentActivity,
            unviewedCount = unviewedCount,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    suspend fun getRandomQuestionId(): Long? {
        return questionRepository.getQuestionIds().randomOrNull()
    }
}
