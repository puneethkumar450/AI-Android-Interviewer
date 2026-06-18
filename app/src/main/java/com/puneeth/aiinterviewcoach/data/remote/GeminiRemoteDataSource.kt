package com.puneeth.aiinterviewcoach.data.remote

import com.puneeth.aiinterviewcoach.data.remote.dto.QuestionAssetDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiRemoteDataSource @Inject constructor(
    private val assetLoader: GeminiPromptFactory,
) {
    fun loadQuestionPacks(): List<QuestionAssetDto> = assetLoader.loadQuestionPacks()
}
