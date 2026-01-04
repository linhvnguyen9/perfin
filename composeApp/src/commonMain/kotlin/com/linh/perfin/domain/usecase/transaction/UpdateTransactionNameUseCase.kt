package com.linh.perfin.domain.usecase.transaction

import com.linh.perfin.domain.repository.transaction.TransactionRepository

class UpdateTransactionNameUseCase(
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(transactionId: String, name: String) = 
        transactionRepository.updateTransactionName(transactionId, name)
}