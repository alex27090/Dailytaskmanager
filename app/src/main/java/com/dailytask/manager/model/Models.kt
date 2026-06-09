package com.dailytask.manager.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.time.LocalDateTime

@Parcelize
data class Task(
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val priority: Priority = Priority.MEDIUM,
    val category: Category? = null,
    val dueDate: LocalDate? = null,
    val reminderTime: LocalDateTime? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) : Parcelable {
    val isOverdue: Boolean
        get() = dueDate != null && !isCompleted && dueDate.isBefore(LocalDate.now())
    val isDueToday: Boolean
        get() = dueDate?.isEqual(LocalDate.now()) == true
}
@Parcelize
data class Category(
    val id: Long = 0,
    val name: String,
    val colorHex: String = "#FFB3C6",
    val iconName: String = "ic_category_default"
) : Parcelable
enum class Priority(val displayName: String, val level: Int) {
    LOW("Low", 0),
    MEDIUM("Medium", 1),
    HIGH("High", 2);

    companion object {
        fun fromLevel(level: Int) = entries.firstOrNull { it.level == level } ?: MEDIUM
    }
}
data class DailySummary(
    val date: LocalDate,
    val totalTasks: Int,
    val completedTasks: Int,
    val pendingTasks: Int,
    val overdueTasks: Int
) {
    val completionRate: Float
        get() = if (totalTasks == 0) 0f else completedTasks.toFloat() / totalTasks
}


