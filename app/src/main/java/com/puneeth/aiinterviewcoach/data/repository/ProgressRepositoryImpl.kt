package com.puneeth.aiinterviewcoach.data.repository

import com.puneeth.aiinterviewcoach.domain.model.InterviewCategory
import com.puneeth.aiinterviewcoach.domain.model.ProgressSummary
import com.puneeth.aiinterviewcoach.domain.model.ResultAnalysis
import com.puneeth.aiinterviewcoach.domain.repository.InterviewRepository
import com.puneeth.aiinterviewcoach.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProgressRepositoryImpl @Inject constructor(
    interviewRepository: InterviewRepository,
) : ProgressRepository {
    private val sessions = interviewRepository.observeCompletedSessions()

    override fun observeProgressSummary(): Flow<ProgressSummary> = sessions.map { items ->
        val scores = items.mapNotNull { it.feedback?.score }
        val latestDay = items.maxOfOrNull { it.completedAt ?: 0L } ?: 0L
        ProgressSummary(
            completedInterviews = items.size,
            currentStreak = if (latestDay == 0L) 0 else minOf(items.size, 7),
            averageScore = if (scores.isEmpty()) 0 else scores.average().toInt(),
            categoryScores = InterviewCategory.entries.associateWith { category ->
                val categoryScores = items.filter { it.category == category }.mapNotNull { it.feedback?.score }
                if (categoryScores.isEmpty()) 0 else categoryScores.average().toInt()
            },
            weakAreas = items.flatMap { it.feedback?.weaknesses.orEmpty() }
                .groupingBy { it }
                .eachCount()
                .entries
                .sortedByDescending { it.value }
                .take(3)
                .map { it.key },
        )
    }

    override fun observeLatestAnalysis(): Flow<ResultAnalysis> = sessions.map { items ->
        val latest = items.firstOrNull()?.feedback
        ResultAnalysis(
            strengths = latest?.strengths.orEmpty(),
            weaknesses = latest?.weaknesses.orEmpty(),
            improvementSuggestions = latest?.improvementSuggestions.orEmpty(),
            recommendedLearningTopics = latest?.recommendedTopics.orEmpty(),
        )
    }
}
