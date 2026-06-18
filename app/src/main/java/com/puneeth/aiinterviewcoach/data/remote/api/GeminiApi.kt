package com.puneeth.aiinterviewcoach.data.remote.api

import com.puneeth.aiinterviewcoach.data.remote.dto.QuestionAssetDto

interface GeminiApi {
    suspend fun loadQuestions(): List<QuestionAssetDto>
}
