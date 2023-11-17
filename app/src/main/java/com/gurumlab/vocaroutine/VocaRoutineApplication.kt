package com.gurumlab.vocaroutine

import android.app.Application
import androidx.room.Room
import com.gurumlab.vocaroutine.data.source.local.AppDatabase
import com.gurumlab.vocaroutine.data.source.local.DataStoreModule

class VocaRoutineApplication : Application() {

    private lateinit var dataStore: DataStoreModule

    override fun onCreate() {
        super.onCreate()
        application = this
        dataStore = DataStoreModule(this)

        db = Room.databaseBuilder(
            this,
            AppDatabase::class.java, "scheduledAlarm"
        ).build()
    }

    fun getDataStore(): DataStoreModule = dataStore

    companion object {
        private lateinit var application: VocaRoutineApplication
        lateinit var db: AppDatabase

        fun getInstance(): VocaRoutineApplication = application
        val appContainer = AppContainer()
    }
}