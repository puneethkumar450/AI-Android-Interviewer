package com.puneeth.aiinterviewcoach.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.puneeth.aiinterviewcoach.data.local.dao.InterviewSessionDao
import com.puneeth.aiinterviewcoach.data.local.dao.QuestionProgressDao
import com.puneeth.aiinterviewcoach.data.local.dao.QuestionDao
import com.puneeth.aiinterviewcoach.data.local.entity.InterviewSessionEntity
import com.puneeth.aiinterviewcoach.data.local.entity.QuestionEntity
import com.puneeth.aiinterviewcoach.data.local.entity.QuestionProgressEntity

@Database(
    entities = [QuestionEntity::class, InterviewSessionEntity::class, QuestionProgressEntity::class],
    version = 2,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun questionDao(): QuestionDao
    abstract fun interviewSessionDao(): InterviewSessionDao
    abstract fun questionProgressDao(): QuestionProgressDao
}
