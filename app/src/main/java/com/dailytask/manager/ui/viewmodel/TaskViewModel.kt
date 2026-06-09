package com.dailytask.manager.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dailytask.manager.data.repository.CategoryRepository
import com.dailytask.manager.data.repository.TaskRepository
import com.dailytask.manager.model.Category
import com.dailytask.manager.model.Priority
import com.dailytask.manager.model.Task
import com.dailytask.manager.util.ReminderScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

sealed class TaskUiState {
    object Idle : TaskUiState()
    object Loading : TaskUiState()
    object Saved : TaskUiState()
    data class Error(val message: String) : TaskUiState()
}

class TaskViewModel(
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository,
    private val reminderScheduler: ReminderScheduler,
    private val taskId: Long
) : ViewModel() {

    val categories: LiveData<List<Category>> = categoryRepository.getAllCategories().asLiveData()

    private val _uiState = MutableLiveData<TaskUiState>(TaskUiState.Idle)
    val uiState: LiveData<TaskUiState> = _uiState

    private val _editingTask = MutableLiveData<Task?>(null)
    val editingTask: LiveData<Task?> = _editingTask

    init {
        if (taskId != 0L) {
            viewModelScope.launch {
                _editingTask.value = taskRepository.getTaskById(taskId)
            }
        }
    }

    fun saveTask(
        title: String,
        description: String,
        priority: Priority,
        category: Category?,
        dueDate: LocalDate?,
        reminderTime: LocalDateTime?
    ) {
        if (title.isBlank()) {
            _uiState.value = TaskUiState.Error("Title cannot be empty")
            return
        }

        viewModelScope.launch {
            _uiState.value = TaskUiState.Loading
            val task = Task(
                id = taskId,
                title = title.trim(),
                description = description.trim(),
                priority = priority,
                category = category,
                dueDate = dueDate,
                reminderTime = reminderTime,
                isCompleted = _editingTask.value?.isCompleted ?: false
            )
            val savedId = taskRepository.saveTask(task)

            // Schedule reminder if set
            reminderTime?.let {
                reminderScheduler.scheduleReminder(task.copy(id = savedId))
            }

            _uiState.value = TaskUiState.Saved
        }
    }

    fun deleteTask() {
        if (taskId == 0L) return
        viewModelScope.launch {
            _uiState.value = TaskUiState.Loading
            taskRepository.deleteTaskById(taskId)
            reminderScheduler.cancelReminder(taskId)
            _uiState.value = TaskUiState.Saved
        }
    }

    fun resetState() {
        _uiState.value = TaskUiState.Idle
    }
}
