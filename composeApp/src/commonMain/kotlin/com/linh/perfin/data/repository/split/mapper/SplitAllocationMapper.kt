package com.linh.perfin.data.repository.split.mapper

import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.linh.perfin.common.utils.toInstant
import com.linh.perfin.domain.model.split.Participant
import com.linh.perfin.domain.model.split.SplitAllocation
import com.perfin.features.expensetracking.SplitAllocationEntity
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun SplitAllocation.toSplitAllocationEntity() = SplitAllocationEntity(
    allocation_id = id,
    split_transaction_id = splitTransactionId,
    participant_id = participant.id,
    allocation_value = allocationValue.toStringExpanded(),
    calculated_amount = calculatedAmount.toStringExpanded(),
    created_at = createdAt.toEpochMilliseconds(),
    updated_at = updatedAt.toEpochMilliseconds()
)

@OptIn(ExperimentalTime::class)
fun SplitAllocationEntity.toSplitAllocation(participant: Participant) = SplitAllocation(
    id = allocation_id,
    splitTransactionId = split_transaction_id,
    participant = participant,
    allocationValue = allocation_value.toBigDecimal(),
    calculatedAmount = calculated_amount.toBigDecimal(),
    createdAt = created_at.toInstant(),
    updatedAt = updated_at.toInstant()
)
