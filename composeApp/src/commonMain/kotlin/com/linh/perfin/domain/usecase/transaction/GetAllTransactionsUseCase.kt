package com.linh.perfin.domain.usecase.transaction

import com.linh.perfin.domain.repository.transaction.TransactionRepository

class GetAllTransactionsUseCase(
    private val transactionRepository: TransactionRepository
) {
    operator fun invoke() = transactionRepository.getAllTransactions()
}