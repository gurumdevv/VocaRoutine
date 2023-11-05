package com.gurumlab.vocaroutine

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activeAlarms")
data class Alarm(
    @PrimaryKey(autoGenerate = true)
    val alarmCode: Int,
    val date: String,
    val content: String
)