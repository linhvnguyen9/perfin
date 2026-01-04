package com.linh.perfin.domain.repository.category

import com.linh.perfin.domain.model.category.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getAllCategories(): Flow<List<Category>>
    suspend fun getCategoryById(id: String): Category?
    suspend fun getCategoriesByType(isIncome: Boolean): List<Category>
    suspend fun getSystemCategories(): List<Category>
    suspend fun getUserCategories(): List<Category>
    suspend fun insertCategory(category: Category)
    suspend fun updateCategory(category: Category)
    suspend fun deleteCategory(id: String)
}