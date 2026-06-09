package com.dailytask.manager

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.dailytask.manager.data.local.dao.CategoryDao
import com.dailytask.manager.data.local.dao.TaskDao
import com.dailytask.manager.data.local.entity.TaskEntity
import com.dailytask.manager.data.repository.TaskRepository
import com.dailytask.manager.model.Priority
import com.dailytask.manager.model.Task
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.*
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class TaskRepositoryTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var taskDao: TaskDao

    @Mock
    private lateinit var categoryDao: CategoryDao

    private lateinit var repository: TaskRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        repository = TaskRepository(taskDao, categoryDao)
    }

    @Test
    fun `saveTask returns generated ID`() = runTest {
        val task = Task(title = "Test task", priority = Priority.HIGH)
        `when`(taskDao.insertTask(any())).thenReturn(42L)

        val result = repository.saveTask(task)

        assertEquals(42L, result)
        verify(taskDao).insertTask(any())
    }

    @Test
    fun `getPendingTasks filters completed tasks`() = runTest {
        val pendingEntity = TaskEntity(
            id = 1L, title = "Pending", isCompleted = false, priority = 1
        )
        `when`(taskDao.getPendingTasks()).thenReturn(flowOf(listOf(pendingEntity)))
        `when`(categoryDao.getAllCategories()).thenReturn(flowOf(emptyList()))

        // Verify only non-completed tasks come through
        val flow = repository.getPendingTasks()
        flow.collect { tasks ->
            assertTrue(tasks.all { !it.isCompleted })
        }
    }

    @Test
    fun `task isOverdue returns true for past due dates`() {
        val task = Task(
            title = "Overdue task",
            dueDate = LocalDate.now().minusDays(1),
            isCompleted = false
        )
        assertTrue(task.isOverdue)
    }

    @Test
    fun `task isOverdue returns false when completed`() {
        val task = Task(
            title = "Done task",
            dueDate = LocalDate.now().minusDays(1),
            isCompleted = true
        )
        assertFalse(task.isOverdue)
    }

    @Test
    fun `task isDueToday returns true for today`() {
        val task = Task(
            title = "Today task",
            dueDate = LocalDate.now()
        )
        assertTrue(task.isDueToday)
    }

    @Test
    fun `setTaskCompleted calls dao with correct args`() = runTest {
        repository.setTaskCompleted(5L, true)
        verify(taskDao).setTaskCompleted(eq(5L), eq(true), anyLong())
    }
}


