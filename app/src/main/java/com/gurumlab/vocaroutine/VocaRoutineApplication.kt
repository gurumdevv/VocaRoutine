package com.gurumlab.vocaroutine

import android.app.Application

class VocaRoutineApplication: Application() {

    private lateinit var dataStore : DataStoreModule

    companion object{
        private lateinit var application : VocaRoutineApplication

        fun getInstance() : VocaRoutineApplication = application
    }

    override fun onCreate() {
        super.onCreate()
        application = this
        dataStore = DataStoreModule(this)
    }

    fun getDataStore() : DataStoreModule = dataStore
}