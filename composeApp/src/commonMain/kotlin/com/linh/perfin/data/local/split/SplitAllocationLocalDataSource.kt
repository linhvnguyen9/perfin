package com.linh.perfin.data.local.split

import com.perfin.features.expensetracking.SplitAllocationEntity
import kotlinx.coroutines.flow.Flow

interface SplitAllocationLocalDataSource {
    suspend fun getAllocationsBySplitId(splitId: String): List<SplitAllocationEntity>
    suspend fun getAllocationById(allocationId: String): SplitAllocationEntity?
    suspend fun insertAllocation(allocation: SplitAllocationEntity)
    suspend fun updateAllocation(allocation: SplitAllocationEntity)
    suspend fun deleteAllocation(allocationId: String)
    suspend fun deleteAllocationsBySplitId(splitId: String)
}
