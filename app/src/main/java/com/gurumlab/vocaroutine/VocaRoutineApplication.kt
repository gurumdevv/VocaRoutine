package com.gurumlab.vocaroutine

import android.app.Application
import androidx.room.Room
import com.gurumlab.vocaroutine.data.source.local.AppDatabase
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class VocaRoutineApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        application = this

        db = Room.databaseBuilder(
            this,
            AppDatabase::class.java, "scheduledAlarm"
        ).build()
    }

    companion object {
        private lateinit var application: VocaRoutineApplication
        lateinit var db: AppDatabase
    }
}