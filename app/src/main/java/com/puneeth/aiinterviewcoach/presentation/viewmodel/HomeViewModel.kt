package com.puneeth.aiinterviewcoach.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puneeth.aiinterviewcoach.domain.model.CategoryProgress
import com.puneeth.aiinterviewcoach.domain.model.CategorySummary
import com.puneeth.aiinterviewcoach.domain.model.HomeDifficultySummary
import com.puneeth.aiinterviewcoach.domain.model.HomeSummary
import com.puneeth.aiinterviewcoach.domain.model.InterviewCategory
import com.puneeth.aiinterviewcoach.domain.model.InterviewDifficulty
import com.puneeth.aiinterviewcoach.domain.model.PracticeQuestion
import com.puneeth.aiinterviewcoach.domain.model.RecentActivity
import com.puneeth.aiinterviewcoach.domain.repository.ProgressRepository
import com.puneeth.aiinterviewcoach.domain.repository.QuestionRepository
import com.puneeth.aiinterviewcoach.domain.repository.UserPreferencesRepository
import com.puneeth.aiinterviewcoach.domain.usecase.ObserveHomeSummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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
    val hardRatedCount: Int = 0,
    val viewedTodayCount: Int = 0,
    val dailyGoal: Int = 5,
    val suggestedQuestions: List<PracticeQuestion> = emptyList(),
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    observeHomeSummary: ObserveHomeSummaryUseCase,
    private val progressRepository: ProgressRepository,
    private val questionRepository: QuestionRepository,
    private val preferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        combine(
            observeHomeSummary(),
            progressRepository.observeProgressSummary(),
            progressRepository.observeRecentActivity(),
        ) { summary, progress, recentActivity ->
            Triple(summary, progress, recentActivity)
        },
        combine(
            progressRepository.observeUnviewedCount(),
            progressRepository.observeHardRatedCount(),
            progressRepository.observeViewedTodayCount(),
        ) { unviewed, hard, today -> Triple(unviewed, hard, today) },
        combine(
            questionRepository.observeSuggestedQuestions(3),
            preferencesRepository.observePreferences().map { it.dailyGoal },
        ) { suggestions, goal -> suggestions to goal },
    ) { (summary, progress, recentActivity), (unviewedCount, hardRatedCount, viewedTodayCount), (suggestions, dailyGoal) ->
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
            hardRatedCount = hardRatedCount,
            viewedTodayCount = viewedTodayCount,
            dailyGoal = dailyGoal,
            suggestedQuestions = suggestions,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    suspend fun getRandomQuestionId(): Long? {
        return questionRepository.getQuestionIds().randomOrNull()
    }

    fun adjustDailyGoal(delta: Int) {
        viewModelScope.launch {
            val current = uiState.value.dailyGoal
            preferencesRepository.updateDailyGoal((current + delta).coerceIn(1, 50))
        }
    }
}
