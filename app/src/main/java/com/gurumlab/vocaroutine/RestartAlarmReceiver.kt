package com.gurumlab.vocaroutine

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class RestartAlarmReceiver : BroadcastReceiver() {

    private lateinit var alarmFunctions: AlarmFunctions
    private val db = VocaRoutineApplication.db.alarmDao()

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action.equals("android.intent.action.BOOT_COMPLETED")) {
            alarmFunctions = AlarmFunctions(context)
            val job = CoroutineScope(Dispatchers.IO).launch {
                val alarmList = db.getAllAlarms()
                alarmList.let {
                    for (alarm in alarmList) {
                        val date = alarm.date
                        val code = alarm.alarmCode
                        val content = alarm.content
                        alarmFunctions.callAlarm(date, code, content)
                    }
                }
            }
            job.invokeOnCompletion {
                it?.printStackTrace()
                CoroutineScope(Dispatchers.IO).cancel()
            }
        }
    }
}