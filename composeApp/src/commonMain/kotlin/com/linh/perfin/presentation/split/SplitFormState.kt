package com.linh.perfin.presentation.split

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.linh.perfin.domain.model.split.Participant
import com.linh.perfin.domain.model.split.SplitMode
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class SplitFormState(
    val splitTransactionId: String? = null,
    val splitMode: SplitMode = SplitMode.EQUAL,
    val allocations: List<AllocationFormItem> = emptyList(),
    val validationError: String? = null,
    val isDirty: Boolean = false
) {
    fun isValid(): Boolean = validationError == null && allocations.size >= 2

    fun getTotalAllocated(): BigDecimal {
        return allocations.fold(BigDecimal.ZERO) { acc, allocation ->
            acc + allocation.calculatedAmount
        }
    }

    fun getTotalInputValue(): BigDecimal {
        return when (splitMode) {
            SplitMode.PERCENTAGE,
            SplitMode.AMOUNT,
            SplitMode.SHARES -> allocations.fold(BigDecimal.ZERO) { acc, allocation ->
                acc + allocation.inputValue
            }
            SplitMode.EQUAL -> BigDecimal.ZERO // Not applicable
        }
    }
}

@OptIn(ExperimentalUuidApi::class)
data class AllocationFormItem(
    val id: String = Uuid.random().toString(),
    val participant: Participant?,
    val inputValue: BigDecimal = BigDecimal.ZERO,
    val calculatedAmount: BigDecimal = BigDecimal.ZERO,
    val error: String? = null
)
