package com.dailytask.manager.data.repository

import com.dailytask.manager.data.local.dao.CategoryDao
import com.dailytask.manager.data.local.dao.TaskDao
import com.dailytask.manager.data.local.toDomain
import com.dailytask.manager.data.local.toEntity
import com.dailytask.manager.data.local.toEpochMilli
import com.dailytask.manager.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate

class TaskRepository(
    private val taskDao: TaskDao,
    private val categoryDao: CategoryDao
) {

    fun getAllTasks(): Flow<List<Task>> =
        combine(taskDao.getAllTasks(), categoryDao.getAllCategories()) { tasks, categories ->
            val categoryMap = categories.associateBy { it.id }
            tasks.map { entity ->
                entity.toDomain(entity.categoryId?.let { categoryMap[it]?.toDomain() })
            }
        }

    fun getTasksForDay(date: LocalDate): Flow<List<Task>> {
        val start = date.toEpochMilli()
        val end = date.plusDays(1).toEpochMilli() - 1
        return combine(taskDao.getTasksForDay(start, end), categoryDao.getAllCategories()) { tasks, categories ->
            val categoryMap = categories.associateBy { it.id }
            tasks.map { entity ->
                entity.toDomain(entity.categoryId?.let { categoryMap[it]?.toDomain() })
            }
        }
    }

    fun getTasksByCategory(categoryId: Long): Flow<List<Task>> =
        combine(taskDao.getTasksByCategory(categoryId), categoryDao.getAllCategories()) { tasks, categories ->
            val categoryMap = categories.associateBy { it.id }
            tasks.map { entity ->
                entity.toDomain(entity.categoryId?.let { categoryMap[it]?.toDomain() })
            }
        }

    fun getPendingTasks(): Flow<List<Task>> =
        combine(taskDao.getPendingTasks(), categoryDao.getAllCategories()) { tasks, categories ->
            val categoryMap = categories.associateBy { it.id }
            tasks.map { entity ->
                entity.toDomain(entity.categoryId?.let { categoryMap[it]?.toDomain() })
            }
        }

    fun getCompletedTasks(): Flow<List<Task>> =
        combine(taskDao.getCompletedTasks(), categoryDao.getAllCategories()) { tasks, categories ->
            val categoryMap = categories.associateBy { it.id }
            tasks.map { entity ->
                entity.toDomain(entity.categoryId?.let { categoryMap[it]?.toDomain() })
            }
        }

    fun searchTasks(query: String): Flow<List<Task>> =
        combine(taskDao.searchTasks(query), categoryDao.getAllCategories()) { tasks, categories ->
            val categoryMap = categories.associateBy { it.id }
            tasks.map { entity ->
                entity.toDomain(entity.categoryId?.let { categoryMap[it]?.toDomain() })
            }
        }

    fun getPendingTaskCount(): Flow<Int> = taskDao.getPendingTaskCount()

    suspend fun getTaskById(id: Long): Task? {
        val entity = taskDao.getTaskById(id) ?: return null
        val cat = entity.categoryId?.let { categoryDao.getCategoryById(it)?.toDomain() }
        return entity.toDomain(cat)
    }

    suspend fun saveTask(task: Task): Long =
        taskDao.insertTask(task.toEntity())

    suspend fun deleteTask(task: Task) =
        taskDao.deleteTask(task.toEntity())

    suspend fun deleteTaskById(id: Long) =
        taskDao.deleteTaskById(id)

    suspend fun setTaskCompleted(id: Long, completed: Boolean) =
        taskDao.setTaskCompleted(id, completed, System.currentTimeMillis())

    suspend fun deleteAllCompletedTasks() =
        taskDao.deleteAllCompletedTasks()
}
