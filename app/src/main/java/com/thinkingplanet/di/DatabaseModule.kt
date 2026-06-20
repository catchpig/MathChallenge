package com.thinkingplanet.di

import android.content.Context
import androidx.room.Room
import com.thinkingplanet.data.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "thinking_planet.db").build()

    @Provides fun provideLevelProgressDao(db: AppDatabase) = db.levelProgressDao()
    @Provides fun provideMedalDao(db: AppDatabase) = db.medalDao()
    @Provides fun provideTrophyDao(db: AppDatabase) = db.trophyDao()
    @Provides fun provideProfileDao(db: AppDatabase) = db.profileDao()
}
