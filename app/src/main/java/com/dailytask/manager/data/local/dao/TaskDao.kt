package com.dailytask.manager.data.local.dao

import androidx.room.*
import com.dailytask.manager.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    // ── Queries ──────────────────────────────────────────────────────────────

    @Query("SELECT * FROM tasks ORDER BY due_date ASC, priority DESC, created_at DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): TaskEntity?

    @Query("""
        SELECT * FROM tasks
        WHERE due_date >= :startOfDay AND due_date <= :endOfDay
        ORDER BY priority DESC, created_at ASC
    """)
    fun getTasksForDay(startOfDay: Long, endOfDay: Long): Flow<List<TaskEntity>>

    @Query("""
        SELECT * FROM tasks
        WHERE category_id = :categoryId
        ORDER BY due_date ASC, priority DESC
    """)
    fun getTasksByCategory(categoryId: Long): Flow<List<TaskEntity>>

    @Query("""
        SELECT * FROM tasks
        WHERE is_completed = 0
        ORDER BY due_date ASC, priority DESC
    """)
    fun getPendingTasks(): Flow<List<TaskEntity>>

    @Query("""
        SELECT * FROM tasks
        WHERE is_completed = 1
        ORDER BY updated_at DESC
    """)
    fun getCompletedTasks(): Flow<List<TaskEntity>>

    @Query("""
        SELECT * FROM tasks
        WHERE title LIKE '%' || :query || '%'
        OR description LIKE '%' || :query || '%'
        ORDER BY due_date ASC
    """)
    fun searchTasks(query: String): Flow<List<TaskEntity>>

    @Query("SELECT COUNT(*) FROM tasks WHERE is_completed = 0")
    fun getPendingTaskCount(): Flow<Int>

    @Query("""
        SELECT * FROM tasks
        WHERE reminder_time IS NOT NULL
        AND reminder_time > :now
        AND is_completed = 0
    """)
    suspend fun getUpcomingReminders(now: Long): List<TaskEntity>

    // ── Mutations ─────────────────────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTaskById(id: Long)

    @Query("UPDATE tasks SET is_completed = :completed, updated_at = :now WHERE id = :id")
    suspend fun setTaskCompleted(id: Long, completed: Boolean, now: Long = System.currentTimeMillis())

    @Query("DELETE FROM tasks WHERE is_completed = 1")
    suspend fun deleteAllCompletedTasks()
}


