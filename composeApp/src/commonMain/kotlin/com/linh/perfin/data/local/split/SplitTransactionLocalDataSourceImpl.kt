package com.linh.perfin.data.local.split

import com.perfin.features.expensetracking.ExpenseTrackingDatabase
import com.perfin.features.expensetracking.SplitTransactionEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class SplitTransactionLocalDataSourceImpl(
    database: ExpenseTrackingDatabase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SplitTransactionLocalDataSource {

    private val queries = database.splitTransactionQueries

    override suspend fun getSplitByTransactionId(transactionId: String): SplitTransactionEntity? {
        return withContext(ioDispatcher) {
            queries.getSplitByTransactionId(transactionId).executeAsOneOrNull()
        }
    }

    override suspend fun getSplitById(splitId: String): SplitTransactionEntity? {
        return withContext(ioDispatcher) {
            queries.getSplitById(splitId).executeAsOneOrNull()
        }
    }

    override suspend fun insertSplitTransaction(split: SplitTransactionEntity) {
        withContext(ioDispatcher) {
            queries.insertSplitTransaction(
                split_transaction_id = split.split_transaction_id,
                parent_transaction_id = split.parent_transaction_id,
                split_mode = split.split_mode,
                created_at = split.created_at,
                updated_at = split.updated_at
            )
        }
    }

    override suspend fun updateSplitTransaction(split: SplitTransactionEntity) {
        withContext(ioDispatcher) {
            queries.updateSplitTransaction(
                splitTransactionId = split.split_transaction_id,
                splitMode = split.split_mode,
                updatedAt = split.updated_at
            )
        }
    }

    override suspend fun deleteSplitTransaction(splitId: String) {
        withContext(ioDispatcher) {
            queries.deleteSplitTransaction(splitId)
        }
    }

    override suspend fun deleteSplitByTransactionId(transactionId: String) {
        withContext(ioDispatcher) {
            queries.deleteSplitByTransactionId(transactionId)
        }
    }
}
