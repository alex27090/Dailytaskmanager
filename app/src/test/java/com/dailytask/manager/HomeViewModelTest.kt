package com.dailytask.manager

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.dailytask.manager.data.repository.TaskRepository
import com.dailytask.manager.model.Priority
import com.dailytask.manager.model.Task
import com.dailytask.manager.ui.viewmodel.HomeViewModel
import com.dailytask.manager.util.ReminderScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    @Mock
    private lateinit var taskRepository: TaskRepository

    @Mock
    private lateinit var reminderScheduler: ReminderScheduler

    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)

        `when`(taskRepository.getTasksForDay(any())).thenReturn(flowOf(emptyList()))
        `when`(taskRepository.getPendingTaskCount()).thenReturn(flowOf(0))

        viewModel = HomeViewModel(taskRepository, reminderScheduler)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial selectedDate is today`() {
        assertEquals(LocalDate.now(), viewModel.selectedDate.value)
    }

    @Test
    fun `selectDate updates selectedDate`() {
        val nextWeek = LocalDate.now().plusDays(7)
        viewModel.selectDate(nextWeek)
        assertEquals(nextWeek, viewModel.selectedDate.value)
    }

    @Test
    fun `dailySummary calculates completionRate correctly`() = runTest {
        val tasks = listOf(
            Task(id = 1, title = "A", isCompleted = true),
            Task(id = 2, title = "B", isCompleted = true),
            Task(id = 3, title = "C", isCompleted = false)
        )
        `when`(taskRepository.getTasksForDay(any())).thenReturn(flowOf(tasks))

        val freshViewModel = HomeViewModel(taskRepository, reminderScheduler)
        val summary = freshViewModel.dailySummary.value

        assertEquals(3, summary.totalTasks)
        assertEquals(2, summary.completedTasks)
        assertEquals(1, summary.pendingTasks)
        // 2/3 ≈ 0.666
        assertTrue(summary.completionRate > 0.6f)
    }

    @Test
    fun `toggleTaskComplete calls repository`() = runTest {
        val task = Task(id = 1L, title = "Toggle me", isCompleted = false)
        viewModel.toggleTaskComplete(task)
        verify(taskRepository).setTaskCompleted(1L, true)
    }

    @Test
    fun `deleteTask calls repository and cancels reminder`() = runTest {
        val task = Task(id = 2L, title = "Delete me")
        viewModel.deleteTask(task)
        verify(taskRepository).deleteTask(task)
        verify(reminderScheduler).cancelReminder(2L)
    }
}


