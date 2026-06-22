package com.puneeth.aiinterviewcoach.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.puneeth.aiinterviewcoach.data.local.entity.QuestionEntity
import com.puneeth.aiinterviewcoach.data.local.model.CategorySummaryRow
import com.puneeth.aiinterviewcoach.data.local.model.DifficultySummaryRow
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {
    @Query(
        """
        SELECT * FROM questions
        WHERE (:searchQuery = '' 
            OR question LIKE '%' || :searchQuery || '%' 
            OR answer LIKE '%' || :searchQuery || '%' 
            OR explanation LIKE '%' || :searchQuery || '%'
            OR category LIKE '%' || :searchQuery || '%'
            OR tags LIKE '%' || :searchQuery || '%')
        AND (:category IS NULL OR category = :category)
        AND (:difficulty IS NULL OR difficulty = :difficulty)
        AND (:bookmarksOnly = 0 OR isBookmarked = 1)
        ORDER BY isBookmarked DESC, category ASC, question ASC
        """,
    )
    fun observeQuestions(
        searchQuery: String,
        category: String?,
        difficulty: String?,
        bookmarksOnly: Boolean,
    ): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE id = :questionId")
    fun observeQuestion(questionId: Long): Flow<QuestionEntity?>

    @Query("SELECT * FROM questions WHERE id = :questionId")
    suspend fun getQuestion(questionId: Long): QuestionEntity?

    @Query(
        """
        SELECT id FROM questions
        WHERE (:searchQuery = '' 
            OR question LIKE '%' || :searchQuery || '%' 
            OR answer LIKE '%' || :searchQuery || '%' 
            OR explanation LIKE '%' || :searchQuery || '%'
            OR category LIKE '%' || :searchQuery || '%'
            OR tags LIKE '%' || :searchQuery || '%')
        AND (:category IS NULL OR category = :category)
        AND (:difficulty IS NULL OR difficulty = :difficulty)
        AND (:bookmarksOnly = 0 OR isBookmarked = 1)
        ORDER BY category ASC, question ASC
        """,
    )
    suspend fun getQuestionIds(
        searchQuery: String,
        category: String?,
        difficulty: String?,
        bookmarksOnly: Boolean,
    ): List<Long>

    @Query("UPDATE questions SET isBookmarked = NOT isBookmarked WHERE id = :questionId")
    suspend fun toggleBookmark(questionId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(questions: List<QuestionEntity>)

    @Query("SELECT COUNT(*) FROM questions")
    suspend fun count(): Int

    @Query("SELECT id FROM questions WHERE isBookmarked = 1")
    suspend fun getBookmarkedQuestionIds(): List<Long>

    @Query("SELECT COUNT(*) FROM questions WHERE isBookmarked = 1")
    fun observeBookmarksCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM questions")
    fun observeTotalCount(): Flow<Int>

    @Query(
        """
        SELECT category, COUNT(*) AS questionCount
        FROM questions
        WHERE (:searchQuery = '' OR category LIKE '%' || :searchQuery || '%')
        GROUP BY category
        ORDER BY category ASC
        """,
    )
    fun observeCategorySummaries(searchQuery: String): Flow<List<CategorySummaryRow>>

    @Query(
        """
        SELECT difficulty AS dimension, COUNT(*) AS questionCount
        FROM questions
        WHERE (:searchQuery = '' OR difficulty LIKE '%' || :searchQuery || '%')
        GROUP BY difficulty
        ORDER BY CASE difficulty
            WHEN 'Beginner' THEN 1
            WHEN 'Intermediate' THEN 2
            WHEN 'Advanced' THEN 3
            WHEN 'Expert' THEN 4
            ELSE 5
        END
        """,
    )
    fun observeDifficultySummaries(searchQuery: String): Flow<List<DifficultySummaryRow>>

    @Query("SELECT * FROM questions WHERE isBookmarked = 1 ORDER BY category ASC, question ASC")
    fun observeBookmarkedQuestions(): Flow<List<QuestionEntity>>
}
