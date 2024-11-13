package com.gurumlab.vocaroutine.di

import com.gurumlab.vocaroutine.util.AlarmCodeManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AlarmCodeManagerModule {

    @Provides
    @Singleton
    fun getAlarmCodeManager(): AlarmCodeManager {
        return AlarmCodeManager()
    }
}