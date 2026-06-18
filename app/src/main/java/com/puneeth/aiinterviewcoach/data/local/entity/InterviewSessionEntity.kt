package com.puneeth.aiinterviewcoach.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "interview_sessions")
data class InterviewSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String,
    val difficulty: String,
    val startedAt: Long,
    val completedAt: Long,
    val score: Int,
    val transcript: String,
    val strengths: String,
    val weaknesses: String,
    val improvementSuggestions: String,
    val recommendedTopics: String,
    val summary: String,
)
