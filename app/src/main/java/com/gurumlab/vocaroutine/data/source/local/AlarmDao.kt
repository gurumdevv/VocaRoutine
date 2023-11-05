package com.gurumlab.vocaroutine.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gurumlab.vocaroutine.data.model.Alarm

@Dao
interface AlarmDao {
    @Query("SELECT * FROM activeAlarms")
    suspend fun getAllAlarms(): List<Alarm>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAlarm(alarm: Alarm)

    @Query("DELETE FROM activeAlarms WHERE alarmCode = :alarmCode")
    suspend fun deleteAlarm(alarmCode: Int)

    @Query("SELECT alarmCode FROM activeAlarms WHERE alarmCode IN (:alarmCodes)")
    suspend fun searchActiveAlarms(alarmCodes: IntArray): List<Int>
}