package com.dailytask.manager.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("category_id")]
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "description")
    val description: String = "",

    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean = false,

    @ColumnInfo(name = "priority")
    val priority: Int = Priority.MEDIUM.value, // 0=Low, 1=Medium, 2=High

    @ColumnInfo(name = "category_id")
    val categoryId: Long? = null,

    @ColumnInfo(name = "due_date")
    val dueDate: Long? = null,         // epoch millis (date only)

    @ColumnInfo(name = "reminder_time")
    val reminderTime: Long? = null,    // epoch millis (date + time)

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)

enum class Priority(val value: Int) {
    LOW(0), MEDIUM(1), HIGH(2);

    companion object {
        fun fromValue(value: Int) = entries.firstOrNull { it.value == value } ?: MEDIUM
    }
}


