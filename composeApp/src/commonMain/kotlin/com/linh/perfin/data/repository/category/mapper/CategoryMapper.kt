package com.linh.perfin.data.repository.category.mapper

import com.linh.perfin.domain.model.category.Category
import com.perfin.features.expensetracking.CategoryEntity
import kotlin.time.Instant

/**
 * Maps CategoryEntity to Category domain model
 */
fun CategoryEntity.toDomain(): Category {
    return Category(
        id = category_id,
        parentCategoryId = parent_category_id,
        name = name,
        icon = icon,
        color = color,
        isIncome = is_income == 1L,
        isSystem = is_system == 1L,
        createdAt = Instant.fromEpochMilliseconds(created_at),
        updatedAt = Instant.fromEpochMilliseconds(updated_at)
    )
}

/**
 * Maps Category domain model to CategoryEntity
 */
fun Category.toEntity(): CategoryEntity {
    return CategoryEntity(
        category_id = id,
        parent_category_id = parentCategoryId,
        name = name,
        icon = icon,
        color = color,
        is_income = if (isIncome) 1L else 0L,
        is_system = if (isSystem) 1L else 0L,
        created_at = createdAt.toEpochMilliseconds(),
        updated_at = updatedAt.toEpochMilliseconds()
    )
}