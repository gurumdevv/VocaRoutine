package com.gurumlab.vocaroutine.di

import android.content.Context
import androidx.room.Room
import com.gurumlab.vocaroutine.data.source.local.AlarmDao
import com.gurumlab.vocaroutine.data.source.local.AppDatabase
import com.gurumlab.vocaroutine.data.source.local.OfflineModeDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java, "VocaRoutineDatabase"
        ).build()
    }

    @Singleton
    @Provides
    fun provideAlarmDao(appDatabase: AppDatabase): AlarmDao {
        return appDatabase.alarmDao()
    }

    @Singleton
    @Provides
    fun provideOfflineModeDao(appDatabase: AppDatabase): OfflineModeDao {
        return appDatabase.offlineModeDao()
    }
}