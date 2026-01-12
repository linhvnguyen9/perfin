package com.linh.perfin.data.local.transaction

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.perfin.features.expensetracking.ExpenseTrackingDatabase
import com.perfin.features.expensetracking.TransactionsEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Clock

class TransactionLocalDataSourceImpl(
    database: ExpenseTrackingDatabase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : TransactionLocalDataSource {

    private val queries = database.transactionQueries

    override fun getAllTransactions(): Flow<List<TransactionsEntity>> {
        return queries.getAllTransactions()
            .asFlow()
            .mapToList(ioDispatcher)
            .map { transactions ->
                transactions.map { transaction ->
                    transaction.copy(
                        amount = transaction.amount,
                        date = transaction.date,
                        is_reconciled = transaction.is_reconciled,
                        notes = transaction.notes,
                        location = transaction.location,
                        created_at = transaction.created_at,
                        updated_at = transaction.updated_at
                    )
                }
            }
    }

    override suspend fun getAllTransactionsByAccount(accountId: String): Flow<List<TransactionsEntity>> {
        return queries.getAllTransactionsByAccount(accountId)
            .asFlow()
            .mapToList(ioDispatcher)
    }

    override suspend fun insertTransaction(transaction: TransactionsEntity) {
        queries.insertTransaction(
            transaction_id = transaction.transaction_id,
            account_id = transaction.account_id,
            amount = transaction.amount,
            title = transaction.title,
            date = transaction.date,
            is_reconciled = transaction.is_reconciled,
            notes = transaction.notes,
            location = transaction.location,
            created_at = transaction.created_at,
            updated_at = transaction.updated_at,
        )
    }

    override suspend fun updateTransactionName(id: String, name: String) {
        queries.updateTransactionName(
            title = name,
            transaction_id = id
        )
    }

    override suspend fun getTransactionById(id: String): TransactionsEntity? {
        return queries.getTransactionById(id).executeAsOneOrNull()
    }

    override suspend fun updateTransaction(transaction: TransactionsEntity) {
        queries.updateTransaction(
            amount = transaction.amount,
            title = transaction.title,
            date = transaction.date,
            notes = transaction.notes,
            location = transaction.location,
            currentTimestamp = Clock.System.now().toEpochMilliseconds(),
            transactionId = transaction.transaction_id
        )
    }

    override suspend fun deleteTransaction(id: String) {
        queries.deleteTransaction(id)
    }

    override suspend fun hasTransactionsByAccount(accountId: String): Boolean {
        return queries.getAllTransactionsByAccount(accountId)
            .executeAsList()
            .isNotEmpty()
    }
}