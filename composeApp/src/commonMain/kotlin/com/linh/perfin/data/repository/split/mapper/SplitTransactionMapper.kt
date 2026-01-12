package com.linh.perfin.data.repository.split.mapper

import com.linh.perfin.common.utils.toInstant
import com.linh.perfin.domain.model.split.SplitAllocation
import com.linh.perfin.domain.model.split.SplitMode
import com.linh.perfin.domain.model.split.SplitTransaction
import com.perfin.features.expensetracking.SplitTransactionEntity
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun SplitTransaction.toSplitTransactionEntity() = SplitTransactionEntity(
    split_transaction_id = id,
    parent_transaction_id = parentTransactionId,
    split_mode = splitMode.name,
    created_at = createdAt.toEpochMilliseconds(),
    updated_at = updatedAt.toEpochMilliseconds()
)

@OptIn(ExperimentalTime::class)
fun SplitTransactionEntity.toSplitTransaction(allocations: List<SplitAllocation>) = SplitTransaction(
    id = split_transaction_id,
    parentTransactionId = parent_transaction_id,
    splitMode = SplitMode.fromString(split_mode),
    allocations = allocations,
    createdAt = created_at.toInstant(),
    updatedAt = updated_at.toInstant()
)
