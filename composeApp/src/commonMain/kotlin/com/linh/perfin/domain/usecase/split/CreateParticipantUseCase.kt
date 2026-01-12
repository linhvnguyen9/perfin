package com.linh.perfin.domain.usecase.split

import com.linh.perfin.domain.model.split.Participant
import com.linh.perfin.domain.repository.split.ParticipantRepository

class CreateParticipantUseCase(
    private val repository: ParticipantRepository
) {
    suspend operator fun invoke(participant: Participant) {
        require(participant.name.isNotBlank()) { "Participant name cannot be empty" }
        repository.insertParticipant(participant)
    }
}
