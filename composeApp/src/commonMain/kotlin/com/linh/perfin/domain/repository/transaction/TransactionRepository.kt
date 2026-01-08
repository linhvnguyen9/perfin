package com.linh.perfin.domain.repository.transaction

import com.linh.perfin.domain.model.transaction.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getAllTransactions(): Flow<List<Transaction>>
    suspend fun getTransactionById(id: String): Transaction?
    suspend fun insertTransaction(transaction: Transaction)
    suspend fun updateTransaction(transaction: Transaction)
    suspend fun updateTransactionName(id: String, name: String)
    suspend fun deleteTransaction(id: String)
    suspend fun hasTransactionsByAccount(accountId: String): Boolean
}