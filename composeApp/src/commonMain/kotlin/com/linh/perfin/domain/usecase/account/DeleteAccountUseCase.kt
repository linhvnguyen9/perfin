package com.linh.perfin.domain.usecase.account

import com.linh.perfin.domain.repository.account.AccountRepository
import com.linh.perfin.domain.repository.transaction.TransactionRepository

class DeleteAccountUseCase(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(id: String) {
        val hasTransactions = transactionRepository.hasTransactionsByAccount(id)
        if (hasTransactions) {
            throw AccountInUseException("Cannot delete account with existing transactions")
        }
        accountRepository.deleteAccount(id)
    }
}