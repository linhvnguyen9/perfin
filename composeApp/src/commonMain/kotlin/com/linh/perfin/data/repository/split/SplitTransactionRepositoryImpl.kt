package com.linh.perfin.data.repository.split

import com.linh.perfin.data.local.split.ParticipantLocalDataSource
import com.linh.perfin.data.local.split.SplitAllocationLocalDataSource
import com.linh.perfin.data.local.split.SplitTransactionLocalDataSource
import com.linh.perfin.data.repository.split.mapper.toParticipant
import com.linh.perfin.data.repository.split.mapper.toSplitAllocation
import com.linh.perfin.data.repository.split.mapper.toSplitAllocationEntity
import com.linh.perfin.data.repository.split.mapper.toSplitTransaction
import com.linh.perfin.data.repository.split.mapper.toSplitTransactionEntity
import com.linh.perfin.domain.model.split.SplitTransaction
import com.linh.perfin.domain.repository.split.SplitTransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SplitTransactionRepositoryImpl(
    private val splitLocalDataSource: SplitTransactionLocalDataSource,
    private val allocationLocalDataSource: SplitAllocationLocalDataSource,
    private val participantLocalDataSource: ParticipantLocalDataSource
) : SplitTransactionRepository {

    override suspend fun getSplitByTransactionId(transactionId: String): SplitTransaction? {
        val splitEntity = splitLocalDataSource.getSplitByTransactionId(transactionId) ?: return null
        val allocationEntities = allocationLocalDataSource.getAllocationsBySplitId(splitEntity.split_transaction_id)

        // Load participants for each allocation
        val allocations = allocationEntities.map { allocationEntity ->
            val participant = participantLocalDataSource.getParticipantById(allocationEntity.participant_id)
                ?: throw IllegalStateException("Participant not found: ${allocationEntity.participant_id}")
            allocationEntity.toSplitAllocation(participant.toParticipant())
        }

        return splitEntity.toSplitTransaction(allocations)
    }

    override suspend fun insertSplitTransaction(split: SplitTransaction) {
        // Delete existing split if any
        deleteSplitByTransactionId(split.parentTransactionId)

        // Insert split
        splitLocalDataSource.insertSplitTransaction(split.toSplitTransactionEntity())

        // Insert allocations
        split.allocations.forEach { allocation ->
            allocationLocalDataSource.insertAllocation(allocation.toSplitAllocationEntity())
        }
    }

    override suspend fun updateSplitTransaction(split: SplitTransaction) {
        // Update split entity
        splitLocalDataSource.updateSplitTransaction(split.toSplitTransactionEntity())

        // Delete old allocations
        allocationLocalDataSource.deleteAllocationsBySplitId(split.id)

        // Insert new allocations
        split.allocations.forEach { allocation ->
            allocationLocalDataSource.insertAllocation(allocation.toSplitAllocationEntity())
        }
    }

    override suspend fun deleteSplitTransaction(splitId: String) {
        // Allocations will be deleted by CASCADE
        splitLocalDataSource.deleteSplitTransaction(splitId)
    }

    override suspend fun deleteSplitByTransactionId(transactionId: String) {
        splitLocalDataSource.deleteSplitByTransactionId(transactionId)
    }

    override fun observeSplitByTransactionId(transactionId: String): Flow<SplitTransaction?> = flow {
        emit(getSplitByTransactionId(transactionId))
    }
}
