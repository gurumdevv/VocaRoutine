package com.gurumlab.vocaroutine

import android.app.Application
import com.gurumlab.vocaroutine.util.NotificationHandler
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class VocaRoutineApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        NotificationHandler(this).createChanel()
    }
}