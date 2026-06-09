package com.dailytask.manager.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.dailytask.manager.R
import com.dailytask.manager.ui.MainActivity

class ReminderReceiver : BroadcastReceiver() {

    companion object {
        const val EXTRA_TASK_ID = "extra_task_id"
        const val EXTRA_TASK_TITLE = "extra_task_title"
        const val EXTRA_TASK_DESC = "extra_task_desc"
        const val CHANNEL_ID = "task_reminders"
        const val CHANNEL_NAME = "Task Reminders"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getLongExtra(EXTRA_TASK_ID, -1L)
        val title = intent.getStringExtra(EXTRA_TASK_TITLE) ?: "Task Reminder"
        val desc = intent.getStringExtra(EXTRA_TASK_DESC) ?: ""

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        ensureChannel(nm)

        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("taskId", taskId)
        }
        val tapPending = PendingIntent.getActivity(
            context, taskId.toInt(), tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(desc.ifBlank { "Time to work on this task!" })
            .setStyle(NotificationCompat.BigTextStyle().bigText(desc.ifBlank { "Time to work on this task!" }))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(tapPending)
            .setAutoCancel(true)
            .build()

        nm.notify(taskId.toInt(), notification)
    }

    private fun ensureChannel(nm: NotificationManager) {
        if (nm.getNotificationChannel(CHANNEL_ID) != null) return
        val channel = NotificationChannel(
            CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Reminders for your daily tasks"
        }
        nm.createNotificationChannel(channel)
    }
}


