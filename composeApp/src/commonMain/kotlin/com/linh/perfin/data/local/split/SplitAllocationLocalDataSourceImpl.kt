package com.linh.perfin.data.local.split

import com.perfin.features.expensetracking.ExpenseTrackingDatabase
import com.perfin.features.expensetracking.SplitAllocationEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class SplitAllocationLocalDataSourceImpl(
    database: ExpenseTrackingDatabase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SplitAllocationLocalDataSource {

    private val queries = database.splitAllocationQueries

    override suspend fun getAllocationsBySplitId(splitId: String): List<SplitAllocationEntity> {
        return withContext(ioDispatcher) {
            queries.getAllocationsBySplitId(splitId).executeAsList()
        }
    }

    override suspend fun getAllocationById(allocationId: String): SplitAllocationEntity? {
        return withContext(ioDispatcher) {
            queries.getAllocationById(allocationId).executeAsOneOrNull()
        }
    }

    override suspend fun insertAllocation(allocation: SplitAllocationEntity) {
        withContext(ioDispatcher) {
            queries.insertAllocation(
                allocation_id = allocation.allocation_id,
                split_transaction_id = allocation.split_transaction_id,
                participant_id = allocation.participant_id,
                allocation_value = allocation.allocation_value,
                calculated_amount = allocation.calculated_amount,
                created_at = allocation.created_at,
                updated_at = allocation.updated_at
            )
        }
    }

    override suspend fun updateAllocation(allocation: SplitAllocationEntity) {
        withContext(ioDispatcher) {
            queries.updateAllocation(
                allocationId = allocation.allocation_id,
                allocationValue = allocation.allocation_value,
                calculatedAmount = allocation.calculated_amount,
                updatedAt = allocation.updated_at
            )
        }
    }

    override suspend fun deleteAllocation(allocationId: String) {
        withContext(ioDispatcher) {
            queries.deleteAllocation(allocationId)
        }
    }

    override suspend fun deleteAllocationsBySplitId(splitId: String) {
        withContext(ioDispatcher) {
            queries.deleteAllocationsBySplitId(splitId)
        }
    }
}
