package com.linh.perfin.domain.usecase.split

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.DecimalMode
import com.ionspin.kotlin.bignum.decimal.RoundingMode
import com.linh.perfin.domain.model.split.SplitAllocation
import com.linh.perfin.domain.model.split.SplitMode

class CalculateSplitAmountsUseCase {
    // Decimal mode for currency calculations: 2 decimal places with ceiling rounding
    private val decimalMode = DecimalMode(
        decimalPrecision = 50,
        roundingMode = RoundingMode.ROUND_HALF_TO_EVEN,
        scale = 2
    )

    operator fun invoke(
        totalAmount: BigDecimal,
        splitMode: SplitMode,
        allocations: List<SplitAllocation>
    ): List<SplitAllocation> {
        if (allocations.isEmpty()) return emptyList()

        val absoluteAmount = if (totalAmount < BigDecimal.ZERO) -totalAmount else totalAmount

        return when (splitMode) {
            SplitMode.EQUAL -> {
                val perPerson = absoluteAmount.divide(BigDecimal.fromInt(allocations.size), decimalMode)
                allocations.map { allocation ->
                    allocation.copy(
                        allocationValue = perPerson,
                        calculatedAmount = perPerson
                    )
                }
            }

            SplitMode.PERCENTAGE -> {
                allocations.map { allocation ->
                    val calculated = (absoluteAmount * allocation.allocationValue).divide(BigDecimal.fromInt(100), decimalMode)
                    allocation.copy(calculatedAmount = calculated)
                }
            }

            SplitMode.AMOUNT -> {
                // Already specified, just use the input value as calculated
                allocations.map { allocation ->
                    allocation.copy(calculatedAmount = allocation.allocationValue)
                }
            }

            SplitMode.SHARES -> {
                val totalShares = allocations.fold(BigDecimal.ZERO) { acc, allocation ->
                    acc + allocation.allocationValue
                }
                if (totalShares == BigDecimal.ZERO) {
                    return allocations
                }
                allocations.map { allocation ->
                    val calculated = (absoluteAmount * allocation.allocationValue).divide(totalShares, decimalMode)
                    allocation.copy(calculatedAmount = calculated)
                }
            }
        }
    }
}
