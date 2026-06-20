package com.thinkingplanet.di

import android.content.Context
import com.thinkingplanet.data.repository.ProgressRepositoryImpl
import com.thinkingplanet.data.repository.QuestionRepositoryImpl
import com.thinkingplanet.data.source.QuestionPackLoader
import com.thinkingplanet.domain.repository.ProgressRepository
import com.thinkingplanet.domain.repository.QuestionRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides @Singleton
    fun provideQuestionPackLoader(@ApplicationContext context: Context) =
        QuestionPackLoader(context)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds @Singleton
    abstract fun bindProgressRepository(impl: ProgressRepositoryImpl): ProgressRepository

    @Binds @Singleton
    abstract fun bindQuestionRepository(impl: QuestionRepositoryImpl): QuestionRepository
}
