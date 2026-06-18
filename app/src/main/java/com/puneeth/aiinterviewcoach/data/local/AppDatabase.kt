package com.puneeth.aiinterviewcoach.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.puneeth.aiinterviewcoach.data.local.dao.InterviewSessionDao
import com.puneeth.aiinterviewcoach.data.local.dao.QuestionDao
import com.puneeth.aiinterviewcoach.data.local.entity.InterviewSessionEntity
import com.puneeth.aiinterviewcoach.data.local.entity.QuestionEntity

@Database(
    entities = [QuestionEntity::class, InterviewSessionEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun questionDao(): QuestionDao
    abstract fun interviewSessionDao(): InterviewSessionDao
}
