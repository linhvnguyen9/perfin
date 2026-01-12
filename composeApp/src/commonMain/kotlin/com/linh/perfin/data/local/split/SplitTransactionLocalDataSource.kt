package com.linh.perfin.data.local.split

import com.perfin.features.expensetracking.SplitTransactionEntity
import kotlinx.coroutines.flow.Flow

interface SplitTransactionLocalDataSource {
    suspend fun getSplitByTransactionId(transactionId: String): SplitTransactionEntity?
    suspend fun getSplitById(splitId: String): SplitTransactionEntity?
    suspend fun insertSplitTransaction(split: SplitTransactionEntity)
    suspend fun updateSplitTransaction(split: SplitTransactionEntity)
    suspend fun deleteSplitTransaction(splitId: String)
    suspend fun deleteSplitByTransactionId(transactionId: String)
}
