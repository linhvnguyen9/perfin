package com.linh.perfin.domain.usecase.split

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.linh.perfin.domain.model.split.SplitMode
import com.linh.perfin.domain.model.split.SplitTransaction

class ValidateSplitTransactionUseCase {
    operator fun invoke(
        parentAmount: BigDecimal,
        split: SplitTransaction
    ): SplitValidationResult {
        if (split.allocations.size < 2) {
            return SplitValidationResult.Error("At least 2 participants required")
        }

        val absoluteAmount = if (parentAmount < BigDecimal.ZERO) -parentAmount else parentAmount

        return when (split.splitMode) {
            SplitMode.EQUAL -> SplitValidationResult.Valid

            SplitMode.PERCENTAGE -> {
                val total = split.allocations.fold(BigDecimal.ZERO) { acc, allocation ->
                    acc + allocation.allocationValue
                }
                if (total != BigDecimal.fromInt(100)) {
                    SplitValidationResult.Error("Percentages must sum to 100% (currently: ${total.toPlainString()}%)")
                } else {
                    SplitValidationResult.Valid
                }
            }

            SplitMode.AMOUNT -> {
                val total = split.getTotalAllocated()
                // Allow small tolerance for rounding errors
                val difference = if (total > absoluteAmount) total - absoluteAmount else absoluteAmount - total
                val tolerance = BigDecimal.fromDouble(0.01)

                if (difference > tolerance) {
                    SplitValidationResult.Error("Amounts must sum to ${absoluteAmount.toPlainString()} (currently: ${total.toPlainString()})")
                } else {
                    SplitValidationResult.Valid
                }
            }

            SplitMode.SHARES -> {
                val hasInvalidShares = split.allocations.any { it.allocationValue <= BigDecimal.ZERO }
                if (hasInvalidShares) {
                    SplitValidationResult.Error("All shares must be greater than 0")
                } else {
                    SplitValidationResult.Valid
                }
            }
        }
    }
}

sealed class SplitValidationResult {
    data object Valid : SplitValidationResult()
    data class Error(val message: String) : SplitValidationResult()
}
