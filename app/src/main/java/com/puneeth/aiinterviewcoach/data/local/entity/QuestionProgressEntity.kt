package com.puneeth.aiinterviewcoach.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "question_progress")
data class QuestionProgressEntity(
    @PrimaryKey val questionId: Long,
    val viewedCount: Int,
    val completedCount: Int,
    val firstViewedAt: Long,
    val lastViewedAt: Long,
    val lastCompletedAt: Long?,
    val confidenceRating: String? = null,
)
