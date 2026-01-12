package com.linh.perfin.domain.repository.split

import com.linh.perfin.domain.model.split.SplitTransaction
import kotlinx.coroutines.flow.Flow

interface SplitTransactionRepository {
    suspend fun getSplitByTransactionId(transactionId: String): SplitTransaction?
    suspend fun insertSplitTransaction(split: SplitTransaction)
    suspend fun updateSplitTransaction(split: SplitTransaction)
    suspend fun deleteSplitTransaction(splitId: String)
    suspend fun deleteSplitByTransactionId(transactionId: String)
    fun observeSplitByTransactionId(transactionId: String): Flow<SplitTransaction?>
}
