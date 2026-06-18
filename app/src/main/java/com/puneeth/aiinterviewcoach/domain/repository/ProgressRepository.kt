package com.puneeth.aiinterviewcoach.domain.repository

import com.puneeth.aiinterviewcoach.domain.model.ProgressSummary
import com.puneeth.aiinterviewcoach.domain.model.ResultAnalysis
import kotlinx.coroutines.flow.Flow

interface ProgressRepository {
    fun observeProgressSummary(): Flow<ProgressSummary>
    fun observeLatestAnalysis(): Flow<ResultAnalysis>
}
