package com.linh.perfin.data.local.split

import com.perfin.features.expensetracking.ParticipantEntity
import kotlinx.coroutines.flow.Flow

interface ParticipantLocalDataSource {
    fun getAllParticipants(): Flow<List<ParticipantEntity>>
    suspend fun getParticipantById(id: String): ParticipantEntity?
    suspend fun insertParticipant(participant: ParticipantEntity)
    suspend fun updateParticipant(participant: ParticipantEntity)
    suspend fun deleteParticipant(id: String)
    fun searchParticipants(query: String): Flow<List<ParticipantEntity>>
}
