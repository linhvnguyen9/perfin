package com.linh.perfin.domain.usecase.split

import com.linh.perfin.domain.model.split.Participant
import com.linh.perfin.domain.repository.split.ParticipantRepository

class GetParticipantByIdUseCase(
    private val participantRepository: ParticipantRepository
) {
    suspend operator fun invoke(id: String): Participant? {
        return participantRepository.getParticipantById(id)
    }
}
