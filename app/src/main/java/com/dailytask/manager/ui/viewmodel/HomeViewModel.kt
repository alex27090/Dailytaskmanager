package com.dailytask.manager.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dailytask.manager.data.repository.TaskRepository
import com.dailytask.manager.model.DailySummary
import com.dailytask.manager.model.Task
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate

class HomeViewModel(
    private val taskRepository: TaskRepository,
    private val reminderScheduler: com.dailytask.manager.util.ReminderScheduler
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: LiveData<LocalDate> = _selectedDate.asLiveData()

    @OptIn(ExperimentalCoroutinesApi::class)
    val tasksForSelectedDay: LiveData<List<Task>> = _selectedDate
        .flatMapLatest { date -> taskRepository.getTasksForDay(date) }
        .asLiveData()

    val pendingCount: LiveData<Int> = taskRepository.getPendingTaskCount().asLiveData()

    @OptIn(ExperimentalCoroutinesApi::class)
    val dailySummary: LiveData<DailySummary> = _selectedDate
        .flatMapLatest { date ->
            taskRepository.getTasksForDay(date).map { tasks ->
                DailySummary(
                    date = date,
                    totalTasks = tasks.size,
                    completedTasks = tasks.count { it.isCompleted },
                    pendingTasks = tasks.count { !it.isCompleted },
                    overdueTasks = tasks.count { it.isOverdue }
                )
            }
        }
        .asLiveData()

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun toggleTaskComplete(task: Task) {
        viewModelScope.launch {
            taskRepository.setTaskCompleted(task.id, !task.isCompleted)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskRepository.deleteTask(task)
            reminderScheduler.cancelReminder(task.id)
        }
    }

    /** Called by the Snackbar "Undo" action after a swipe-delete */
    fun saveDeletedTask(task: Task) {
        viewModelScope.launch {
            taskRepository.saveTask(task)
        }
    }
}
