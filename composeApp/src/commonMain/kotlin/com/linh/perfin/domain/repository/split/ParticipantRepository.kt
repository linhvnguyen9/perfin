package com.linh.perfin.domain.repository.split

import com.linh.perfin.domain.model.split.Participant
import kotlinx.coroutines.flow.Flow

interface ParticipantRepository {
    fun getAllParticipants(): Flow<List<Participant>>
    suspend fun getParticipantById(id: String): Participant?
    suspend fun insertParticipant(participant: Participant)
    suspend fun updateParticipant(participant: Participant)
    suspend fun deleteParticipant(id: String)
    fun searchParticipants(query: String): Flow<List<Participant>>
}
