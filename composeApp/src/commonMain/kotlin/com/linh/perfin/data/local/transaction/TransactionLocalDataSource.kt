package com.linh.perfin.data.local.transaction

import com.perfin.features.expensetracking.TransactionsEntity
import kotlinx.coroutines.flow.Flow

interface TransactionLocalDataSource {
    fun getAllTransactions(): Flow<List<TransactionsEntity>>
    suspend fun getAllTransactionsByAccount(accountId: String): Flow<List<TransactionsEntity>>
    suspend fun insertTransaction(transaction: TransactionsEntity)
    suspend fun updateTransactionName(id: String, name: String)
}