package com.linh.perfin.data.local.split

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.perfin.features.expensetracking.ExpenseTrackingDatabase
import com.perfin.features.expensetracking.ParticipantEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ParticipantLocalDataSourceImpl(
    database: ExpenseTrackingDatabase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ParticipantLocalDataSource {

    private val queries = database.participantQueries

    override fun getAllParticipants(): Flow<List<ParticipantEntity>> {
        return queries.getAllParticipants()
            .asFlow()
            .mapToList(ioDispatcher)
    }

    override suspend fun getParticipantById(id: String): ParticipantEntity? {
        return withContext(ioDispatcher) {
            queries.getParticipantById(id).executeAsOneOrNull()
        }
    }

    override suspend fun insertParticipant(participant: ParticipantEntity) {
        withContext(ioDispatcher) {
            queries.insertParticipant(
                participant_id = participant.participant_id,
                name = participant.name,
                email = participant.email,
                phone = participant.phone,
                avatar_url = participant.avatar_url,
                created_at = participant.created_at,
                updated_at = participant.updated_at
            )
        }
    }

    override suspend fun updateParticipant(participant: ParticipantEntity) {
        withContext(ioDispatcher) {
            queries.updateParticipant(
                participantId = participant.participant_id,
                name = participant.name,
                email = participant.email,
                phone = participant.phone,
                avatarUrl = participant.avatar_url,
                updatedAt = participant.updated_at
            )
        }
    }

    override suspend fun deleteParticipant(id: String) {
        withContext(ioDispatcher) {
            queries.deleteParticipant(id)
        }
    }

    override fun searchParticipants(query: String): Flow<List<ParticipantEntity>> {
        return queries.searchParticipants(query)
            .asFlow()
            .mapToList(ioDispatcher)
    }
}
