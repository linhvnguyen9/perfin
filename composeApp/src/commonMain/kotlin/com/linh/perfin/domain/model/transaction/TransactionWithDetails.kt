package com.linh.perfin.domain.model.transaction

/**
 * Enhanced transaction model that includes resolved details for UI display
 */
data class TransactionWithDetails(
    val transaction: Transaction,
    val accountName: String,
    val categoryName: String?,
    val categoryIcon: String?,
    val categoryColor: String?
)