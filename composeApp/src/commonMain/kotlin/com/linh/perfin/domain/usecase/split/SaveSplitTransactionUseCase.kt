package com.linh.perfin.domain.usecase.split

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.linh.perfin.domain.model.split.SplitTransaction
import com.linh.perfin.domain.repository.split.SplitTransactionRepository
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class SaveSplitTransactionUseCase(
    private val repository: SplitTransactionRepository,
    private val validateUseCase: ValidateSplitTransactionUseCase,
    private val calculateUseCase: CalculateSplitAmountsUseCase
) {
    suspend operator fun invoke(
        parentTransactionId: String,
        parentAmount: BigDecimal,
        split: SplitTransaction
    ) {
        // Recalculate amounts to ensure consistency
        val calculatedAllocations = calculateUseCase(
            parentAmount,
            split.splitMode,
            split.allocations
        )

        val updatedSplit = split.copy(
            parentTransactionId = parentTransactionId,
            allocations = calculatedAllocations,
            updatedAt = Clock.System.now()
        )

        // Validate
        val validation = validateUseCase(parentAmount, updatedSplit)
        if (validation is SplitValidationResult.Error) {
            throw IllegalStateException(validation.message)
        }

        // Save
        if (split.id.isNotEmpty() && split.id != "new") {
            repository.updateSplitTransaction(updatedSplit)
        } else {
            repository.insertSplitTransaction(updatedSplit)
        }
    }
}
