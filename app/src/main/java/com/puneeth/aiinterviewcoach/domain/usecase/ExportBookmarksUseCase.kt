package com.puneeth.aiinterviewcoach.domain.usecase

import com.puneeth.aiinterviewcoach.domain.repository.QuestionRepository
import java.io.File
import javax.inject.Inject

class ExportBookmarksUseCase @Inject constructor(
    private val repository: QuestionRepository,
) {
    suspend operator fun invoke(destinationDir: File): File {
        return repository.exportBookmarks(destinationDir)
    }
}
