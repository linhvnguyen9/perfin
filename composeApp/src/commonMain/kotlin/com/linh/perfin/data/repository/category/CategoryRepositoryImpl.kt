package com.linh.perfin.data.repository.category

import com.linh.perfin.data.local.category.CategoryLocalDataSource
import com.linh.perfin.data.repository.category.mapper.toDomain
import com.linh.perfin.data.repository.category.mapper.toEntity
import com.linh.perfin.domain.model.category.Category
import com.linh.perfin.domain.repository.category.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CategoryRepositoryImpl(
    private val localDataSource: CategoryLocalDataSource
) : CategoryRepository {

    override fun getAllCategories(): Flow<List<Category>> {
        return localDataSource.getAllCategories().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getCategoryById(id: String): Category? {
        return localDataSource.getCategoryById(id)?.toDomain()
    }

    override suspend fun getCategoriesByType(isIncome: Boolean): List<Category> {
        return localDataSource.getCategoriesByType(isIncome).map { it.toDomain() }
    }

    override suspend fun getSystemCategories(): List<Category> {
        return localDataSource.getSystemCategories().map { it.toDomain() }
    }

    override suspend fun getUserCategories(): List<Category> {
        return localDataSource.getUserCategories().map { it.toDomain() }
    }

    override suspend fun insertCategory(category: Category) {
        localDataSource.insertCategory(category.toEntity())
    }

    override suspend fun updateCategory(category: Category) {
        localDataSource.updateCategory(category.toEntity())
    }

    override suspend fun deleteCategory(id: String) {
        localDataSource.deleteCategory(id)
    }
}