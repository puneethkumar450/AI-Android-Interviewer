package com.puneeth.aiinterviewcoach.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String,
    val difficulty: String,
    val question: String,
    val idealAnswer: String,
    val bookmarked: Boolean,
    val tags: String,
)
