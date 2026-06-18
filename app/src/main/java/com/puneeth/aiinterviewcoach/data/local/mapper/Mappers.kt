package com.puneeth.aiinterviewcoach.data.local.mapper

import com.puneeth.aiinterviewcoach.data.local.entity.InterviewSessionEntity
import com.puneeth.aiinterviewcoach.data.local.entity.QuestionEntity
import com.puneeth.aiinterviewcoach.domain.model.InterviewCategory
import com.puneeth.aiinterviewcoach.domain.model.InterviewDifficulty
import com.puneeth.aiinterviewcoach.domain.model.InterviewMessage
import com.puneeth.aiinterviewcoach.domain.model.InterviewSession
import com.puneeth.aiinterviewcoach.domain.model.MessageRole
import com.puneeth.aiinterviewcoach.domain.model.PracticeQuestion
import com.puneeth.aiinterviewcoach.domain.model.SessionFeedback

fun QuestionEntity.toDomain(): PracticeQuestion = PracticeQuestion(
    id = id,
    category = InterviewCategory.valueOf(category),
    difficulty = InterviewDifficulty.valueOf(difficulty),
    question = question,
    idealAnswer = idealAnswer,
    bookmarked = bookmarked,
    tags = tags.split(",").filter { it.isNotBlank() },
)

fun InterviewSession.toEntity(): InterviewSessionEntity = InterviewSessionEntity(
    id = id,
    category = category.name,
    difficulty = difficulty.name,
    startedAt = startedAt,
    completedAt = completedAt ?: startedAt,
    score = feedback?.score ?: 0,
    transcript = messages.joinToString("\n") { "${it.role.name}: ${it.content}" },
    strengths = feedback?.strengths?.joinToString("|").orEmpty(),
    weaknesses = feedback?.weaknesses?.joinToString("|").orEmpty(),
    improvementSuggestions = feedback?.improvementSuggestions?.joinToString("|").orEmpty(),
    recommendedTopics = feedback?.recommendedTopics?.joinToString("|").orEmpty(),
    summary = feedback?.summary.orEmpty(),
)

fun InterviewSessionEntity.toDomain(): InterviewSession = InterviewSession(
    id = id,
    category = InterviewCategory.valueOf(category),
    difficulty = InterviewDifficulty.valueOf(difficulty),
    startedAt = startedAt,
    completedAt = completedAt,
    messages = transcript.lines().filter { it.isNotBlank() }.mapIndexed { index, line ->
        val role = line.substringBefore(":").trim()
        val content = line.substringAfter(":").trim()
        InterviewMessage(
            id = "$id-$index",
            role = MessageRole.entries.firstOrNull { it.name == role } ?: MessageRole.SYSTEM,
            content = content,
            timestamp = completedAt,
        )
    },
    feedback = SessionFeedback(
        score = score,
        strengths = strengths.split("|").filter { it.isNotBlank() },
        weaknesses = weaknesses.split("|").filter { it.isNotBlank() },
        improvementSuggestions = improvementSuggestions.split("|").filter { it.isNotBlank() },
        recommendedTopics = recommendedTopics.split("|").filter { it.isNotBlank() },
        summary = summary,
    ),
)
