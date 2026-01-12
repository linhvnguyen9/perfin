package com.linh.perfin.domain.usecase.split

import com.linh.perfin.domain.model.split.SplitTransaction
import com.linh.perfin.domain.repository.split.SplitTransactionRepository

class GetSplitByTransactionIdUseCase(
    private val repository: SplitTransactionRepository
) {
    suspend operator fun invoke(transactionId: String): SplitTransaction? {
        return repository.getSplitByTransactionId(transactionId)
    }
}
