package com.puneeth.aiinterviewcoach.data.remote

import com.puneeth.aiinterviewcoach.BuildConfig
import com.puneeth.aiinterviewcoach.data.remote.api.GeminiApi
import com.puneeth.aiinterviewcoach.data.remote.dto.GeminiContent
import com.puneeth.aiinterviewcoach.data.remote.dto.GeminiPart
import com.puneeth.aiinterviewcoach.data.remote.dto.GeminiRequest
import javax.inject.Inject

class GeminiRemoteDataSource @Inject constructor(
    private val api: GeminiApi,
) {
    suspend fun generateText(prompt: String): String {
        if (BuildConfig.GEMINI_API_KEY.isBlank()) {
            return ""
        }
        val response = api.generateInterviewContent(
            apiKey = BuildConfig.GEMINI_API_KEY,
            request = GeminiRequest(
                contents = listOf(
                    GeminiContent(parts = listOf(GeminiPart(text = prompt))),
                ),
            ),
        )
        return response.candidates
            .firstOrNull()
            ?.content
            ?.parts
            ?.joinToString("\n") { it.text }
            .orEmpty()
    }
}
