package com.linh.perfin.data.repository.split

import com.linh.perfin.data.local.split.ParticipantLocalDataSource
import com.linh.perfin.data.repository.split.mapper.toParticipant
import com.linh.perfin.data.repository.split.mapper.toParticipantEntity
import com.linh.perfin.domain.model.split.Participant
import com.linh.perfin.domain.repository.split.ParticipantRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ParticipantRepositoryImpl(
    private val localDataSource: ParticipantLocalDataSource
) : ParticipantRepository {

    override fun getAllParticipants(): Flow<List<Participant>> {
        return localDataSource.getAllParticipants().map { entities ->
            entities.map { it.toParticipant() }
        }
    }

    override suspend fun getParticipantById(id: String): Participant? {
        return localDataSource.getParticipantById(id)?.toParticipant()
    }

    override suspend fun insertParticipant(participant: Participant) {
        localDataSource.insertParticipant(participant.toParticipantEntity())
    }

    override suspend fun updateParticipant(participant: Participant) {
        localDataSource.updateParticipant(participant.toParticipantEntity())
    }

    override suspend fun deleteParticipant(id: String) {
        localDataSource.deleteParticipant(id)
    }

    override fun searchParticipants(query: String): Flow<List<Participant>> {
        return localDataSource.searchParticipants(query).map { entities ->
            entities.map { it.toParticipant() }
        }
    }
}
