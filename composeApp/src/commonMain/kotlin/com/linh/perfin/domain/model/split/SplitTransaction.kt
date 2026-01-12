package com.linh.perfin.domain.model.split

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
data class SplitTransaction(
    val id: String = Uuid.random().toString(),
    val parentTransactionId: String,
    val splitMode: SplitMode,
    val allocations: List<SplitAllocation> = emptyList(),
    val createdAt: Instant,
    val updatedAt: Instant
) {
    fun getTotalAllocated(): BigDecimal {
        return allocations.fold(BigDecimal.ZERO) { acc, allocation ->
            acc + allocation.calculatedAmount
        }
    }

    fun isValid(parentAmount: BigDecimal): Boolean {
        if (allocations.size < 2) return false

        return when (splitMode) {
            SplitMode.EQUAL -> allocations.isNotEmpty()

            SplitMode.PERCENTAGE -> {
                val totalPercentage = allocations.fold(BigDecimal.ZERO) { acc, allocation ->
                    acc + allocation.allocationValue
                }
                totalPercentage == BigDecimal.fromInt(100) && allocations.size >= 2
            }

            SplitMode.AMOUNT -> {
                val absParentAmount = if (parentAmount < BigDecimal.ZERO) -parentAmount else parentAmount
                getTotalAllocated() == absParentAmount && allocations.size >= 2
            }

            SplitMode.SHARES -> allocations.isNotEmpty() && allocations.size >= 2
        }
    }
}
