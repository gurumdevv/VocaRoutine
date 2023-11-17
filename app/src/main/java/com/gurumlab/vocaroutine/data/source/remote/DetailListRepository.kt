package com.gurumlab.vocaroutine.data.source.remote

import com.gurumlab.vocaroutine.data.model.Alarm
import com.gurumlab.vocaroutine.data.source.local.AppDatabase

class DetailListRepository(database: AppDatabase) {

    private val dao = database.alarmDao()

    suspend fun addAlarm(alarm: Alarm) {
        dao.addAlarm(alarm)
    }

    suspend fun deleteAlarm(alarmCode: Int) {
        dao.deleteAlarm(alarmCode)
    }

    suspend fun searchActiveAlarms(alarms: IntArray): List<Int> {
        return dao.searchActiveAlarms(alarms)
    }
}