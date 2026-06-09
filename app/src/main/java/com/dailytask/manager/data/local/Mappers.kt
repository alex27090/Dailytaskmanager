package com.dailytask.manager.data.local

import com.dailytask.manager.data.local.entity.CategoryEntity
import com.dailytask.manager.data.local.entity.Priority as EntityPriority
import com.dailytask.manager.data.local.entity.TaskEntity
import com.dailytask.manager.model.Category
import com.dailytask.manager.model.Priority
import com.dailytask.manager.model.Task
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

// ─── Category Mappers ─────────────────────────────────────────────────────────

fun CategoryEntity.toDomain() = Category(
    id = id,
    name = name,
    colorHex = colorHex,
    iconName = iconName
)

fun Category.toEntity() = CategoryEntity(
    id = id,
    name = name,
    colorHex = colorHex,
    iconName = iconName
)

// ─── Task Mappers ─────────────────────────────────────────────────────────────

fun TaskEntity.toDomain(category: Category? = null) = Task(
    id = id,
    title = title,
    description = description,
    isCompleted = isCompleted,
    priority = Priority.fromLevel(priority),
    category = category,
    dueDate = dueDate?.toLocalDate(),
    reminderTime = reminderTime?.toLocalDateTime(),
    createdAt = createdAt.toLocalDateTime(),
    updatedAt = updatedAt.toLocalDateTime()
)

fun Task.toEntity() = TaskEntity(
    id = id,
    title = title,
    description = description,
    isCompleted = isCompleted,
    priority = priority.level,
    categoryId = category?.id,
    dueDate = dueDate?.toEpochMilli(),
    reminderTime = reminderTime?.toEpochMilli(),
    createdAt = createdAt.toEpochMilli(),
    updatedAt = LocalDateTime.now().toEpochMilli()
)

// ─── Epoch Helpers ────────────────────────────────────────────────────────────

private val zone: ZoneId = ZoneId.systemDefault()

fun Long.toLocalDate(): LocalDate =
    Instant.ofEpochMilli(this).atZone(zone).toLocalDate()

fun Long.toLocalDateTime(): LocalDateTime =
    Instant.ofEpochMilli(this).atZone(zone).toLocalDateTime()

fun LocalDate.toEpochMilli(): Long =
    atStartOfDay(zone).toInstant().toEpochMilli()

fun LocalDateTime.toEpochMilli(): Long =
    atZone(zone).toInstant().toEpochMilli()


