package com.puneeth.aiinterviewcoach.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.puneeth.aiinterviewcoach.data.local.entity.QuestionProgressEntity
import com.puneeth.aiinterviewcoach.data.local.model.LastViewedRow
import com.puneeth.aiinterviewcoach.data.local.model.ProgressSummaryRow
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionProgressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(progress: QuestionProgressEntity)

    @Query("SELECT * FROM question_progress WHERE questionId = :questionId")
    suspend fun getProgress(questionId: Long): QuestionProgressEntity?

    @Query("SELECT COUNT(*) FROM question_progress WHERE viewedCount > 0")
    fun observeViewedCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM question_progress WHERE completedCount > 0")
    fun observeCompletedCount(): Flow<Int>

    @Query(
        """
        SELECT q.category AS dimension,
               SUM(CASE WHEN p.completedCount > 0 THEN 1 ELSE 0 END) AS completedCount,
               COUNT(q.id) AS totalCount
        FROM questions q
        LEFT JOIN question_progress p ON q.id = p.questionId
        GROUP BY q.category
        ORDER BY q.category ASC
        """,
    )
    fun observeCategoryProgress(): Flow<List<ProgressSummaryRow>>

    @Query(
        """
        SELECT q.difficulty AS dimension,
               SUM(CASE WHEN p.completedCount > 0 THEN 1 ELSE 0 END) AS completedCount,
               COUNT(q.id) AS totalCount
        FROM questions q
        LEFT JOIN question_progress p ON q.id = p.questionId
        GROUP BY q.difficulty
        ORDER BY q.difficulty ASC
        """,
    )
    fun observeDifficultyProgress(): Flow<List<ProgressSummaryRow>>

    @Query(
        """
        SELECT q.category AS category, p.lastViewedAt AS lastViewedAt
        FROM question_progress p
        JOIN questions q ON q.id = p.questionId
        ORDER BY p.lastViewedAt DESC
        LIMIT 1
        """,
    )
    fun observeLastViewed(): Flow<LastViewedRow?>

    @Query(
        """
        SELECT COUNT(*) FROM questions q
        LEFT JOIN question_progress p ON q.id = p.questionId
        WHERE p.questionId IS NULL OR p.viewedCount = 0
        """,
    )
    fun observeUnviewedCount(): Flow<Int>

    @Query("UPDATE question_progress SET confidenceRating = :rating WHERE questionId = :questionId")
    suspend fun updateConfidenceRating(questionId: Long, rating: String?)

    @Query("SELECT confidenceRating FROM question_progress WHERE questionId = :questionId")
    fun observeConfidenceRating(questionId: Long): Flow<String?>

    @Query("DELETE FROM question_progress")
    suspend fun clearAll()
}
