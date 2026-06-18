package com.puneeth.aiinterviewcoach.di

import com.puneeth.aiinterviewcoach.data.repository.AiInterviewRepositoryImpl
import com.puneeth.aiinterviewcoach.data.repository.InterviewRepositoryImpl
import com.puneeth.aiinterviewcoach.data.repository.ProgressRepositoryImpl
import com.puneeth.aiinterviewcoach.data.repository.QuestionRepositoryImpl
import com.puneeth.aiinterviewcoach.data.repository.UserPreferencesRepositoryImpl
import com.puneeth.aiinterviewcoach.domain.repository.AiInterviewRepository
import com.puneeth.aiinterviewcoach.domain.repository.InterviewRepository
import com.puneeth.aiinterviewcoach.domain.repository.ProgressRepository
import com.puneeth.aiinterviewcoach.domain.repository.QuestionRepository
import com.puneeth.aiinterviewcoach.domain.repository.UserPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindQuestionRepository(impl: QuestionRepositoryImpl): QuestionRepository

    @Binds
    @Singleton
    abstract fun bindAiInterviewRepository(impl: AiInterviewRepositoryImpl): AiInterviewRepository

    @Binds
    @Singleton
    abstract fun bindProgressRepository(impl: ProgressRepositoryImpl): ProgressRepository

    @Binds
    @Singleton
    abstract fun bindUserPreferencesRepository(impl: UserPreferencesRepositoryImpl): UserPreferencesRepository
}
