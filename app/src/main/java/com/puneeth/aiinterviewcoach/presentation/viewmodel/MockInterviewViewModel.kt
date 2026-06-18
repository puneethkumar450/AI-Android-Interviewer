package com.puneeth.aiinterviewcoach.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puneeth.aiinterviewcoach.domain.model.InterviewCategory
import com.puneeth.aiinterviewcoach.domain.model.InterviewDifficulty
import com.puneeth.aiinterviewcoach.domain.model.InterviewMessage
import com.puneeth.aiinterviewcoach.domain.model.InterviewSession
import com.puneeth.aiinterviewcoach.domain.model.MessageRole
import com.puneeth.aiinterviewcoach.domain.model.SessionFeedback
import com.puneeth.aiinterviewcoach.domain.repository.AiInterviewRepository
import com.puneeth.aiinterviewcoach.domain.usecase.SaveInterviewResultUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

data class MockInterviewUiState(
    val category: InterviewCategory = InterviewCategory.ANDROID_ARCHITECTURE,
    val difficulty: InterviewDifficulty = InterviewDifficulty.SENIOR,
    val messages: List<InterviewMessage> = emptyList(),
    val currentAnswer: String = "",
    val currentQuestion: String = "",
    val isLoading: Boolean = false,
    val latestFeedback: String = "",
    val provisionalScore: Int = 0,
    val sessionComplete: Boolean = false,
    val finalFeedback: SessionFeedback? = null,
)

@HiltViewModel
class MockInterviewViewModel @Inject constructor(
    private val aiInterviewRepository: AiInterviewRepository,
    private val saveInterviewResult: SaveInterviewResultUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(MockInterviewUiState())
    val uiState: StateFlow<MockInterviewUiState> = _uiState.asStateFlow()
    private var sessionStartedAt: Long = System.currentTimeMillis()

    fun updateCategory(category: InterviewCategory) {
        _uiState.value = _uiState.value.copy(category = category)
    }

    fun updateDifficulty(difficulty: InterviewDifficulty) {
        _uiState.value = _uiState.value.copy(difficulty = difficulty)
    }

    fun updateAnswer(answer: String) {
        _uiState.value = _uiState.value.copy(currentAnswer = answer)
    }

    fun startInterview() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, messages = emptyList(), sessionComplete = false, finalFeedback = null)
            sessionStartedAt = System.currentTimeMillis()
            val question = aiInterviewRepository.startInterview(_uiState.value.category, _uiState.value.difficulty)
            val interviewerMessage = InterviewMessage(
                id = randomId(),
                role = MessageRole.INTERVIEWER,
                content = question,
                timestamp = System.currentTimeMillis(),
            )
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                currentQuestion = question,
                messages = listOf(interviewerMessage),
            )
        }
    }

    fun submitAnswer() {
        val answer = _uiState.value.currentAnswer.trim()
        if (answer.isBlank() || _uiState.value.currentQuestion.isBlank()) return
        viewModelScope.launch {
            val candidateMessage = InterviewMessage(
                id = randomId(),
                role = MessageRole.CANDIDATE,
                content = answer,
                timestamp = System.currentTimeMillis(),
            )
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                messages = _uiState.value.messages + candidateMessage,
                currentAnswer = "",
            )
            val turn = aiInterviewRepository.continueInterview(
                category = _uiState.value.category,
                difficulty = _uiState.value.difficulty,
                currentQuestion = _uiState.value.currentQuestion,
                userAnswer = answer,
            )
            val interviewerMessage = InterviewMessage(
                id = randomId(),
                role = MessageRole.INTERVIEWER,
                content = turn.nextQuestion,
                timestamp = System.currentTimeMillis(),
            )
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                currentQuestion = turn.nextQuestion,
                provisionalScore = turn.provisionalScore,
                latestFeedback = turn.quickFeedback,
                messages = _uiState.value.messages + interviewerMessage,
            )
        }
    }

    fun finishInterview() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val transcript = _uiState.value.messages.joinToString("\n") { "${it.role}: ${it.content}" }
            val feedback = aiInterviewRepository.evaluateInterview(
                transcript = transcript,
                category = _uiState.value.category,
                difficulty = _uiState.value.difficulty,
            )
            val session = InterviewSession(
                id = 0,
                category = _uiState.value.category,
                difficulty = _uiState.value.difficulty,
                startedAt = sessionStartedAt,
                completedAt = System.currentTimeMillis(),
                messages = _uiState.value.messages,
                feedback = feedback,
            )
            saveInterviewResult(session)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                sessionComplete = true,
                finalFeedback = feedback,
            )
        }
    }

    private fun randomId(): String = Random.nextLong().toString()
}
