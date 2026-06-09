package com.dailytask.manager.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dailytask.manager.data.repository.CategoryRepository
import com.dailytask.manager.model.Category
import kotlinx.coroutines.launch

import com.dailytask.manager.data.repository.TaskRepository
import com.dailytask.manager.model.Task
import kotlinx.coroutines.flow.combine

class CategoryViewModel(
    private val categoryRepository: CategoryRepository,
    private val taskRepository: TaskRepository
) : ViewModel() {

    val categoriesWithTasks: LiveData<List<Pair<Category, List<Task>>>> = 
        combine(categoryRepository.getAllCategories(), taskRepository.getPendingTasks()) { categories, tasks ->
            val tasksGrouped = tasks.groupBy { it.category?.id }
            categories.map { it to (tasksGrouped[it.id] ?: emptyList()) }
        }.asLiveData()

    val categories: LiveData<List<Category>> = categoryRepository.getAllCategories().asLiveData()

    fun saveCategory(name: String, colorHex: String, iconName: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            categoryRepository.saveCategory(
                Category(name = name.trim(), colorHex = colorHex, iconName = iconName)
            )
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            categoryRepository.deleteCategory(category)
        }
    }
}
