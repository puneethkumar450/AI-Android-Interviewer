package com.puneeth.aiinterviewcoach.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puneeth.aiinterviewcoach.domain.model.InterviewCategory
import com.puneeth.aiinterviewcoach.domain.model.PracticeQuestion
import com.puneeth.aiinterviewcoach.domain.repository.MockInterviewRepository
import com.puneeth.aiinterviewcoach.domain.repository.QuestionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MockSetupState(
    val questionCount: Int = 10,
    val timeLimitSeconds: Int = 60,
    val category: InterviewCategory? = null,
)

data class MockRunningState(
    val questions: List<PracticeQuestion>,
    val currentIndex: Int,
    val answeredCount: Int,
    val timeLeftSeconds: Int,
    val timeLimitSeconds: Int,
    val sessionId: Long,
) {
    val currentQuestion: PracticeQuestion get() = questions[currentIndex]
    val isLast: Boolean get() = currentIndex == questions.lastIndex
    val progress: Float get() = currentIndex.toFloat() / questions.size
}

data class MockResultState(
    val totalQuestions: Int,
    val answeredCount: Int,
    val timeLimitSeconds: Int,
    val category: String?,
    val durationMs: Long,
) {
    val skippedCount: Int get() = totalQuestions - answeredCount
    val scorePercent: Int get() = if (totalQuestions == 0) 0 else (answeredCount * 100) / totalQuestions
}

sealed class MockInterviewUiState {
    data class Setup(val config: MockSetupState = MockSetupState()) : MockInterviewUiState()
    data class Running(val state: MockRunningState) : MockInterviewUiState()
    data class Result(val state: MockResultState) : MockInterviewUiState()
}

@HiltViewModel
class MockInterviewViewModel @Inject constructor(
    private val questionRepository: QuestionRepository,
    private val mockInterviewRepository: MockInterviewRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<MockInterviewUiState>(MockInterviewUiState.Setup())
    val uiState: StateFlow<MockInterviewUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var sessionStartMs: Long = 0L

    fun setQuestionCount(count: Int) {
        val setup = (_uiState.value as? MockInterviewUiState.Setup) ?: return
        _uiState.value = MockInterviewUiState.Setup(setup.config.copy(questionCount = count))
    }

    fun setTimeLimit(seconds: Int) {
        val setup = (_uiState.value as? MockInterviewUiState.Setup) ?: return
        _uiState.value = MockInterviewUiState.Setup(setup.config.copy(timeLimitSeconds = seconds))
    }

    fun setCategory(category: InterviewCategory?) {
        val setup = (_uiState.value as? MockInterviewUiState.Setup) ?: return
        _uiState.value = MockInterviewUiState.Setup(setup.config.copy(category = category))
    }

    fun startSession() {
        val setup = (_uiState.value as? MockInterviewUiState.Setup) ?: return
        val config = setup.config
        viewModelScope.launch {
            val ids = questionRepository.getQuestionIds(category = config.category)
                .shuffled()
                .take(config.questionCount)
            if (ids.isEmpty()) return@launch
            val questions = ids.mapNotNull { questionRepository.getQuestion(it) }
            if (questions.isEmpty()) return@launch

            val sessionId = mockInterviewRepository.startSession(
                totalQuestions = questions.size,
                timeLimitSeconds = config.timeLimitSeconds,
                category = config.category?.title,
            )
            sessionStartMs = System.currentTimeMillis()

            _uiState.value = MockInterviewUiState.Running(
                MockRunningState(
                    questions = questions,
                    currentIndex = 0,
                    answeredCount = 0,
                    timeLeftSeconds = config.timeLimitSeconds,
                    timeLimitSeconds = config.timeLimitSeconds,
                    sessionId = sessionId,
                ),
            )
            startTimer()
        }
    }

    fun markAnswered() = advanceQuestion(answered = true)

    fun skip() = advanceQuestion(answered = false)

    private fun advanceQuestion(answered: Boolean) {
        val running = (_uiState.value as? MockInterviewUiState.Running)?.state ?: return
        val newAnswered = running.answeredCount + if (answered) 1 else 0
        if (running.isLast) {
            finishSession(running, newAnswered)
            return
        }
        timerJob?.cancel()
        _uiState.value = MockInterviewUiState.Running(
            running.copy(
                currentIndex = running.currentIndex + 1,
                answeredCount = newAnswered,
                timeLeftSeconds = running.timeLimitSeconds,
            ),
        )
        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1_000L)
                val running = (_uiState.value as? MockInterviewUiState.Running)?.state ?: break
                val newTime = running.timeLeftSeconds - 1
                if (newTime <= 0) {
                    advanceQuestion(answered = false)
                    break
                } else {
                    _uiState.value = MockInterviewUiState.Running(running.copy(timeLeftSeconds = newTime))
                }
            }
        }
    }

    private fun finishSession(running: MockRunningState, answeredCount: Int) {
        timerJob?.cancel()
        viewModelScope.launch {
            mockInterviewRepository.completeSession(running.sessionId, answeredCount)
        }
        _uiState.value = MockInterviewUiState.Result(
            MockResultState(
                totalQuestions = running.questions.size,
                answeredCount = answeredCount,
                timeLimitSeconds = running.timeLimitSeconds,
                category = running.questions.firstOrNull()?.category?.title,
                durationMs = System.currentTimeMillis() - sessionStartMs,
            ),
        )
    }

    fun resetToSetup() {
        timerJob?.cancel()
        _uiState.value = MockInterviewUiState.Setup()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
