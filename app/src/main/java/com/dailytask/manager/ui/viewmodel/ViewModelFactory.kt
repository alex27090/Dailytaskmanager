package com.dailytask.manager.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dailytask.manager.data.local.TaskDatabase
import com.dailytask.manager.data.repository.CategoryRepository
import com.dailytask.manager.data.repository.TaskRepository
import com.dailytask.manager.util.ReminderScheduler

class ViewModelFactory(private val context: Context, private val taskId: Long = 0) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val db = TaskDatabase.getInstance(context)
        val taskDao = db.taskDao()
        val categoryDao = db.categoryDao()
        
        val taskRepository = TaskRepository(taskDao, categoryDao)
        val categoryRepository = CategoryRepository(categoryDao)
        val scheduler = ReminderScheduler(context)

        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(taskRepository, scheduler) as T
            }
            modelClass.isAssignableFrom(TaskViewModel::class.java) -> {
                TaskViewModel(taskRepository, categoryRepository, scheduler, taskId) as T
            }
            modelClass.isAssignableFrom(CategoryViewModel::class.java) -> {
                CategoryViewModel(categoryRepository, taskRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
