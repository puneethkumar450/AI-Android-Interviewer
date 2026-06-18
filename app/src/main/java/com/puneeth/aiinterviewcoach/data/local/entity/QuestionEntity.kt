package com.puneeth.aiinterviewcoach.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey val id: Long,
    val category: String,
    val difficulty: String,
    val question: String,
    val answer: String,
    val explanation: String,
    val tags: String,
    val isBookmarked: Boolean,
)
