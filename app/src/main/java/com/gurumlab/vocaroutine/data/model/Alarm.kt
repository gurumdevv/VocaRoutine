package com.gurumlab.vocaroutine.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activeAlarms")
data class Alarm(
    @PrimaryKey(autoGenerate = true)
    val alarmCode: Int,
    val id: String,
    val date: String,
    val content: String
)