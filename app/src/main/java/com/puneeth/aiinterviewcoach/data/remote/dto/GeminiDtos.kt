package com.puneeth.aiinterviewcoach.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class QuestionAssetDto(
    val id: Long,
    val category: String,
    val difficulty: String,
    val question: String,
    val answer: String,
    val explanation: String,
    val tags: List<String>,
    val isBookmarked: Boolean = false,
)
