package com.gurumlab.vocaroutine

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NotificationHandler(private val context: Context) {

    fun createChanel() {
        val channelID = ChannelId.REVIEW_CHANNEL
        val channelName = context.getString(R.string.review_notification)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val reviewChannel = NotificationChannel(channelID, channelName, importance)
            .apply {
                description = context.getString(R.string.review_channel_description)
            }

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(reviewChannel)
    }

    fun createNotification(
        channelId: String,
        title: String,
        content: String,
        iconRes: Int,
        pendingIntent: PendingIntent
    ) {
        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(iconRes)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                return
            }
        }
        NotificationManagerCompat.from(context)
            .notify(NotificationId.REVIEW_NOTIFICATION, notification)
    }
}

object ChannelId {
    const val REVIEW_CHANNEL = "reviewChannel"
}

object NotificationId {
    const val REVIEW_NOTIFICATION = 0
}