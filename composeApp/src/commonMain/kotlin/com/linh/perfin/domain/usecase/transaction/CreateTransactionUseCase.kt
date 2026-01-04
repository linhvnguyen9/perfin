package com.linh.perfin.domain.usecase.transaction

import com.linh.perfin.domain.model.transaction.Transaction
import com.linh.perfin.domain.repository.transaction.TransactionRepository

class CreateTransactionUseCase(
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(transaction: Transaction) = transactionRepository.insertTransaction(transaction)
}