package com.puneeth.aiinterviewcoach.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puneeth.aiinterviewcoach.domain.model.ConfidenceRating
import com.puneeth.aiinterviewcoach.domain.model.InterviewCategory
import com.puneeth.aiinterviewcoach.domain.model.InterviewDifficulty
import com.puneeth.aiinterviewcoach.domain.model.PracticeQuestion
import com.puneeth.aiinterviewcoach.domain.repository.ProgressRepository
import com.puneeth.aiinterviewcoach.domain.repository.QuestionRepository
import com.puneeth.aiinterviewcoach.domain.repository.UserPreferences
import com.puneeth.aiinterviewcoach.domain.repository.UserPreferencesRepository
import com.puneeth.aiinterviewcoach.domain.usecase.ObserveQuestionSessionUseCase
import com.puneeth.aiinterviewcoach.domain.usecase.TrackQuestionCompletedUseCase
import com.puneeth.aiinterviewcoach.domain.usecase.TrackQuestionViewedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

data class QuestionsUiState(
    val title: String = "Practice",
    val questionIds: List<Long> = emptyList(),
    val currentIndex: Int = -1,
    val question: PracticeQuestion? = null,
    val showAnswer: Boolean = false,
    val preferences: UserPreferences = UserPreferences(),
    val confidenceRating: ConfidenceRating? = null,
) {
    val hasPrevious: Boolean
        get() = currentIndex > 0
    val hasNext: Boolean
        get() = currentIndex >= 0 && currentIndex < questionIds.lastIndex
}

@HiltViewModel
class QuestionsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val observeQuestionSession: ObserveQuestionSessionUseCase,
    private val questionRepository: QuestionRepository,
    private val progressRepository: ProgressRepository,
    private val trackQuestionViewed: TrackQuestionViewedUseCase,
    private val trackQuestionCompleted: TrackQuestionCompletedUseCase,
    preferencesRepository: UserPreferencesRepository,
) : ViewModel() {
    private val category = savedStateHandle.get<String?>("category")?.takeIf { it.isNotBlank() }?.let(InterviewCategory::fromTitle)
    private val difficulty = savedStateHandle.get<String?>("difficulty")?.takeIf { it.isNotBlank() }?.let(InterviewDifficulty::fromTitle)
    private val search = savedStateHandle.get<String?>("search").orEmpty()
    private val bookmarksOnly = savedStateHandle.get<Boolean>("bookmarksOnly") ?: false
    private val initialStartId = savedStateHandle.get<Long>("startId")?.takeIf { it > 0L }

    private val _uiState = MutableStateFlow(QuestionsUiState())
    val uiState: StateFlow<QuestionsUiState> = _uiState.asStateFlow()

    init {
        loadSession(initialStartId)
        viewModelScope.launch {
            preferencesRepository.observePreferences().collect { preferences ->
                _uiState.value = _uiState.value.copy(preferences = preferences)
            }
        }
        viewModelScope.launch {
            _uiState
                .map { it.question?.id }
                .distinctUntilChanged()
                .flatMapLatest { id ->
                    if (id != null) progressRepository.observeConfidenceRating(id)
                    else flowOf(null)
                }
                .collect { rating ->
                    _uiState.value = _uiState.value.copy(confidenceRating = rating)
                }
        }
    }

    fun revealAnswer() {
        _uiState.value = _uiState.value.copy(showAnswer = true)
        _uiState.value.question?.let { question ->
            viewModelScope.launch {
                trackQuestionCompleted(question.id)
            }
        }
    }

    fun nextQuestion() {
        val nextId = _uiState.value.questionIds.getOrNull(_uiState.value.currentIndex + 1) ?: return
        loadSession(nextId)
    }

    fun previousQuestion() {
        val prevId = _uiState.value.questionIds.getOrNull(_uiState.value.currentIndex - 1) ?: return
        loadSession(prevId)
    }

    fun saveConfidenceRating(rating: ConfidenceRating) {
        val questionId = _uiState.value.question?.id ?: return
        viewModelScope.launch {
            progressRepository.saveConfidenceRating(questionId, rating)
        }
    }

    fun toggleBookmark() {
        val question = _uiState.value.question ?: return
        viewModelScope.launch {
            questionRepository.toggleBookmark(question.id)
            loadSession(question.id)
        }
    }

    private fun loadSession(startQuestionId: Long?) {
        viewModelScope.launch {
            observeQuestionSession(
                startQuestionId = startQuestionId,
                category = category,
                difficulty = difficulty,
                searchQuery = search,
                bookmarksOnly = bookmarksOnly,
            ).collect { session ->
                session.question?.let { trackQuestionViewed(it.id) }
                _uiState.value = QuestionsUiState(
                    title = category?.title ?: if (bookmarksOnly) "Bookmarks" else "Practice",
                    questionIds = session.questionIds,
                    currentIndex = session.currentIndex,
                    question = session.question,
                    showAnswer = false,
                    preferences = _uiState.value.preferences,
                )
            }
        }
    }
}
