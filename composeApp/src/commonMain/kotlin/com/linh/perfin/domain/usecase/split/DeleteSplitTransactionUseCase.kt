package com.linh.perfin.domain.usecase.split

import com.linh.perfin.domain.repository.split.SplitTransactionRepository

class DeleteSplitTransactionUseCase(
    private val repository: SplitTransactionRepository
) {
    suspend operator fun invoke(transactionId: String) {
        repository.deleteSplitByTransactionId(transactionId)
    }
}
