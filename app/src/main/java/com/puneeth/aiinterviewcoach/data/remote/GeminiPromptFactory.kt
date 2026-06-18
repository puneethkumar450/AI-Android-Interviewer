package com.puneeth.aiinterviewcoach.data.remote

import android.content.res.AssetManager
import com.puneeth.aiinterviewcoach.data.remote.dto.QuestionAssetDto
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.serialization.json.Json

@Singleton
class GeminiPromptFactory @Inject constructor(
    private val assetManager: AssetManager,
    private val json: Json,
) {
    fun loadQuestionPacks(directory: String = QUESTION_PACKS_DIR): List<QuestionAssetDto> {
        val files = assetManager.list(directory).orEmpty()
            .filter { it.endsWith(".json") }
            .sorted()
        return files.flatMap { fileName ->
            val raw = assetManager.open("$directory/$fileName").bufferedReader().use { it.readText() }
            json.decodeFromString<List<QuestionAssetDto>>(raw)
        }
    }

    companion object {
        const val QUESTION_PACKS_DIR = "question_packs"
    }
}
