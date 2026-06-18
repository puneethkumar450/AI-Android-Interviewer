package com.puneeth.aiinterviewcoach.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.puneeth.aiinterviewcoach.data.local.entity.QuestionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {
    @Query(
        """
        SELECT * FROM questions
        WHERE (:searchQuery = '' OR question LIKE '%' || :searchQuery || '%' OR idealAnswer LIKE '%' || :searchQuery || '%')
        AND (:category IS NULL OR category = :category)
        AND (:difficulty IS NULL OR difficulty = :difficulty)
        AND (:bookmarksOnly = 0 OR bookmarked = 1)
        ORDER BY bookmarked DESC, question ASC
        """,
    )
    fun observeQuestions(
        searchQuery: String,
        category: String?,
        difficulty: String?,
        bookmarksOnly: Boolean,
    ): Flow<List<QuestionEntity>>

    @Query("UPDATE questions SET bookmarked = NOT bookmarked WHERE id = :questionId")
    suspend fun toggleBookmark(questionId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(questions: List<QuestionEntity>)

    @Query("SELECT COUNT(*) FROM questions")
    suspend fun count(): Int
}
