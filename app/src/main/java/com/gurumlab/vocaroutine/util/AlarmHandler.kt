package com.gurumlab.vocaroutine.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.gurumlab.vocaroutine.R
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class AlarmHandler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val crashlytics: FirebaseCrashlytics
) {

    private lateinit var pendingIntent: PendingIntent
    private lateinit var alarmManager: AlarmManager

    fun callAlarm(date: String, alarmCode: Int, content: String): Boolean {
        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val receiverIntent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("alarmRequestCode", alarmCode)
            putExtra("content", content)
        }

        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()
        ) {
            navigateToNotificationSetting()
        } else {
            pendingIntent = PendingIntent.getBroadcast(
                context,
                alarmCode,
                receiverIntent,
                PendingIntent.FLAG_IMMUTABLE
            )

            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            var dateTime = Date()
            try {
                dateTime = dateFormat.parse(date) as Date
            } catch (e: ParseException) {
                crashlytics.log("${e.message}")
            }

            val calendar = Calendar.getInstance()
            calendar.time = dateTime

            try {
                if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                    return true
                } else {
                    navigateToNotificationSetting()
                }
            } catch (e: SecurityException) {
                navigateToReminderSetting()
            }
        }
        return false
    }

    private fun navigateToNotificationSetting() {
        val intent = Intent().apply {
            action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
        Toast.makeText(
            context,
            context.getString(R.string.check_alarm_permission), Toast.LENGTH_SHORT
        ).show()
    }

    private fun navigateToReminderSetting() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
            Toast.makeText(
                context,
                context.getString(R.string.check_alarm_reminders_permission),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun cancelAlarm(alarmCode: Int) {
        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        pendingIntent =
            PendingIntent.getBroadcast(context, alarmCode, intent, PendingIntent.FLAG_IMMUTABLE)
        alarmManager.cancel(pendingIntent)
    }
}