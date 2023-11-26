package com.gurumlab.vocaroutine

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.gurumlab.vocaroutine.data.source.local.AlarmDao
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RestartAlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var alarmHandler: AlarmHandler
    @Inject
    lateinit var alarmDao: AlarmDao

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action.equals("android.intent.action.BOOT_COMPLETED")) {
            val job = CoroutineScope(Dispatchers.IO).launch {
                val alarmList = alarmDao.getAllAlarms()
                alarmList.let {
                    for (alarm in alarmList) {
                        val date = alarm.date
                        val code = alarm.alarmCode
                        val content = alarm.content
                        alarmHandler.callAlarm(date, code, content)
                    }
                }
            }
            job.invokeOnCompletion {
                it?.printStackTrace()
                job.cancel()
            }
        }
    }
}