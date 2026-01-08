package com.linh.perfin.data.repository.transaction

import com.linh.perfin.data.repository.transaction.mapper.toTransaction
import com.linh.perfin.data.repository.transaction.mapper.toTransactionEntity
import com.linh.perfin.data.local.transaction.TransactionLocalDataSource
import com.linh.perfin.domain.model.transaction.Transaction
import com.linh.perfin.domain.repository.transaction.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TransactionRepositoryImpl(
    private val localDataSource: TransactionLocalDataSource
): TransactionRepository {

    override fun getAllTransactions(): Flow<List<Transaction>> {
        return localDataSource.getAllTransactions().map { it.map { it.toTransaction() } }
    }

    override suspend fun getTransactionById(id: String): Transaction? {
        return localDataSource.getTransactionById(id)?.toTransaction()
    }

    override suspend fun insertTransaction(transaction: Transaction) {
        localDataSource.insertTransaction(transaction.toTransactionEntity())
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        localDataSource.updateTransaction(transaction.toTransactionEntity())
    }

    override suspend fun updateTransactionName(id: String, name: String) {
        localDataSource.updateTransactionName(id, name)
    }

    override suspend fun deleteTransaction(id: String) {
        localDataSource.deleteTransaction(id)
    }

    override suspend fun hasTransactionsByAccount(accountId: String): Boolean {
        return localDataSource.hasTransactionsByAccount(accountId)
    }
}