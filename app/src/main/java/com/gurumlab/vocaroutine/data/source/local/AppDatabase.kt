package com.gurumlab.vocaroutine.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gurumlab.vocaroutine.data.model.Alarm
import com.gurumlab.vocaroutine.data.model.ListInfo

@Database(entities = [Alarm::class, ListInfo::class], version = 1)
@TypeConverters(
    ReviewTypeConverter::class,
    VocabularyListTypeConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun alarmDao(): AlarmDao
    abstract fun offlineModeDao(): OfflineModeDao
}