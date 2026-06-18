package com.puneeth.aiinterviewcoach.data.remote.api

import com.puneeth.aiinterviewcoach.data.remote.dto.GeminiRequest
import com.puneeth.aiinterviewcoach.data.remote.dto.GeminiResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface GeminiApi {
    @POST("v1beta/models/gemini-1.5-flash:generateContent")
    suspend fun generateInterviewContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest,
    ): GeminiResponse
}
