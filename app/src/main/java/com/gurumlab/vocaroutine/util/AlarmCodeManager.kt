package com.gurumlab.vocaroutine.util

import java.time.LocalDateTime
import kotlin.random.Random

class AlarmCodeManager {

    fun getAlarmCode(): Int {
        val nano = LocalDateTime.now().nano
        val randomNum = Random.Default.nextInt(100000000, 1000000000)
        return ((nano + randomNum) / 10) * 10
    }
}