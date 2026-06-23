package com.puneeth.aiinterviewcoach.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mock_interview_sessions")
data class MockInterviewSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val startedAt: Long,
    val completedAt: Long? = null,
    val totalQuestions: Int,
    val answeredCount: Int = 0,
    val timeLimitSeconds: Int,
    val category: String? = null,
)
