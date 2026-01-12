package com.linh.perfin.domain.usecase.split

import com.linh.perfin.domain.model.split.Participant
import com.linh.perfin.domain.repository.split.ParticipantRepository
import kotlinx.coroutines.flow.Flow

class GetAllParticipantsUseCase(
    private val repository: ParticipantRepository
) {
    operator fun invoke(): Flow<List<Participant>> {
        return repository.getAllParticipants()
    }
}
