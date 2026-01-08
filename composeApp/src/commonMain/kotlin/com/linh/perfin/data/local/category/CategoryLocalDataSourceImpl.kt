package com.linh.perfin.data.local.category

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.db.QueryResult
import com.perfin.features.expensetracking.CategoryEntity
import com.perfin.features.expensetracking.ExpenseTrackingDatabase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class CategoryLocalDataSourceImpl(
    database: ExpenseTrackingDatabase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : CategoryLocalDataSource {

    private val queries = database.categoryQueries

    override fun getAllCategories(): Flow<List<CategoryEntity>> {
        return queries
            .getAllCategories()
            .asFlow()
            .mapToList(ioDispatcher)
    }

    override suspend fun getCategoryById(id: String): CategoryEntity? = withContext(ioDispatcher) {
        queries
            .getCategoryById(id)
            .executeAsOneOrNull()
    }

    override suspend fun getCategoriesByType(isIncome: Boolean): List<CategoryEntity> = withContext(ioDispatcher) {
        queries
            .getCategoriesByType(if (isIncome) 1L else 0L)
            .executeAsList()
    }

    override suspend fun getSystemCategories(): List<CategoryEntity> = withContext(ioDispatcher) {
        queries
            .getSystemCategories()
            .executeAsList()
    }

    override suspend fun getUserCategories(): List<CategoryEntity> = withContext(ioDispatcher) {
        queries
            .getUserCategories()
            .executeAsList()
    }

    override suspend fun insertCategory(category: CategoryEntity) {
        return withContext(ioDispatcher) {
            queries.insertCategory(
                category_id = category.category_id,
                parent_category_id = category.parent_category_id,
                name = category.name,
                icon = category.icon,
                color = category.color,
                is_income = category.is_income,
                is_system = category.is_system,
                created_at = category.created_at,
                updated_at = category.updated_at
            )
        }
    }

    override suspend fun updateCategory(category: CategoryEntity) {
        return withContext(ioDispatcher) {
            queries.updateCategory(
                parent_category_id = category.parent_category_id,
                name = category.name,
                icon = category.icon,
                color = category.color,
                is_income = category.is_income,
                is_system = category.is_system,
                updated_at = category.updated_at,
                category_id = category.category_id
            )
        }
    }

    override suspend fun deleteCategory(id: String) {
        return withContext(ioDispatcher) {
            queries.deleteCategory(id)
        }
    }
}