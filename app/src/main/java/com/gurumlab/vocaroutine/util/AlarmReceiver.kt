package com.gurumlab.vocaroutine.util

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.gurumlab.vocaroutine.R
import com.gurumlab.vocaroutine.ui.MainActivity

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val title = intent?.extras!!.getString("content")
        val requestCode = intent.extras!!.getInt("alarmRequestCode")

        val startingIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            requestCode,
            startingIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        NotificationHandler(context!!).createNotification(
            ChannelId.REVIEW_CHANNEL,
            title!!,
            context.getString(R.string.time_to_review),
            R.drawable.ic_logo,
            pendingIntent
        )
    }
}