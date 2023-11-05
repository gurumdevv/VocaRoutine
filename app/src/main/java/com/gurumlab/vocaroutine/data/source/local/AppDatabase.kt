package com.gurumlab.vocaroutine.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gurumlab.vocaroutine.data.model.Alarm

@Database(entities = [Alarm::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun alarmDao(): AlarmDao
}