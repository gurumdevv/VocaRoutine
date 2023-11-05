package com.gurumlab.vocaroutine

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.gurumlab.vocaroutine.ui.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {

    private lateinit var notificationManager: NotificationManager
    private lateinit var builder: NotificationCompat.Builder
    private val db = VocaRoutineApplication.db.alarmDao()

    override fun onReceive(context: Context?, intent: Intent?) {
        notificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelID = "reviewChannel"
        val channelName = context.getString(R.string.review_notification)
        val requestCode = intent?.extras!!.getInt("alarmRequestCode")
        val title = intent.extras!!.getString("content")
        val channelDescription = context.getString(R.string.review_channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT

        val notificationChannel = NotificationChannel(channelID, channelName, importance)
        notificationChannel.description = channelDescription
        notificationManager.createNotificationChannel(notificationChannel)

        builder = NotificationCompat.Builder(context, channelID)

        val startingIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            requestCode,
            startingIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = builder.setContentTitle(title)
            .setContentText(context.getString(R.string.time_to_review))
            .setSmallIcon(R.drawable.ic_logo)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(1, notification)

        val job = CoroutineScope(Dispatchers.IO).launch {
            removeScheduleFromDB(requestCode)
        }
        job.invokeOnCompletion {
            it?.printStackTrace()
            CoroutineScope(Dispatchers.IO).cancel()
        }
    }

    private suspend fun removeScheduleFromDB(alarmCode: Int) {
        db.deleteAlarm(alarmCode)
    }
}