package com.puneeth.aiinterviewcoach.di

import android.content.Context
import androidx.room.Room
import com.puneeth.aiinterviewcoach.data.local.AppDatabase
import com.puneeth.aiinterviewcoach.data.local.dao.InterviewSessionDao
import com.puneeth.aiinterviewcoach.data.local.dao.QuestionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "coach.db",
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideQuestionDao(database: AppDatabase): QuestionDao = database.questionDao()

    @Provides
    fun provideInterviewSessionDao(database: AppDatabase): InterviewSessionDao = database.interviewSessionDao()
}
