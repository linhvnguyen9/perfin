package com.linh.perfin.domain.model.category

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Domain model for transaction categories
 */
@OptIn(ExperimentalTime::class)
data class Category(
    val id: String,
    val parentCategoryId: String?,
    val name: String,
    val icon: String?,
    val color: String?,
    val isIncome: Boolean,
    val isSystem: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant
)