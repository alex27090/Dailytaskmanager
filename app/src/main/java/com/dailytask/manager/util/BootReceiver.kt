package com.dailytask.manager.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.dailytask.manager.data.local.TaskDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val db = TaskDatabase.getInstance(context)
        val scheduler = ReminderScheduler(context)

        // Using IO scope to query database and reschedule alarms
        CoroutineScope(Dispatchers.IO).launch {
            val upcomingTasks = db.taskDao().getUpcomingReminders(System.currentTimeMillis())
            upcomingTasks.forEach { entity ->
                val task = toDomain(entity)
                scheduler.scheduleReminder(task)
            }
        }
    }
}

// Helper to avoid importing full mapper in receiver
private fun toDomain(
    entity: com.dailytask.manager.data.local.entity.TaskEntity
) = com.dailytask.manager.model.Task(
    id = entity.id,
    title = entity.title,
    description = entity.description,
    reminderTime = entity.reminderTime?.let {
        java.time.Instant.ofEpochMilli(it)
            .atZone(java.time.ZoneId.systemDefault())
            .toLocalDateTime()
    }
)
