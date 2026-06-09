package com.dailytask.manager.data.repository

import com.dailytask.manager.data.local.dao.CategoryDao
import com.dailytask.manager.data.local.toDomain
import com.dailytask.manager.data.local.toEntity
import com.dailytask.manager.model.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CategoryRepository(
    private val categoryDao: CategoryDao
) {

    fun getAllCategories(): Flow<List<Category>> =
        categoryDao.getAllCategories().map { it.map { entity -> entity.toDomain() } }

    suspend fun getCategoryById(id: Long): Category? =
        categoryDao.getCategoryById(id)?.toDomain()

    suspend fun saveCategory(category: Category): Long =
        categoryDao.insertCategory(category.toEntity())

    suspend fun deleteCategory(category: Category) =
        categoryDao.deleteCategory(category.toEntity())

    fun getPendingCountForCategory(categoryId: Long): Flow<Int> =
        categoryDao.getPendingCountForCategory(categoryId)
}
