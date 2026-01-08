package com.linh.perfin.data.local.category

import com.perfin.features.expensetracking.CategoryEntity
import kotlinx.coroutines.flow.Flow

interface CategoryLocalDataSource {
    fun getAllCategories(): Flow<List<CategoryEntity>>
    suspend fun getCategoryById(id: String): CategoryEntity?
    suspend fun getCategoriesByType(isIncome: Boolean): List<CategoryEntity>
    suspend fun getSystemCategories(): List<CategoryEntity>
    suspend fun getUserCategories(): List<CategoryEntity>
    suspend fun insertCategory(category: CategoryEntity)
    suspend fun updateCategory(category: CategoryEntity)
    suspend fun deleteCategory(id: String)
}