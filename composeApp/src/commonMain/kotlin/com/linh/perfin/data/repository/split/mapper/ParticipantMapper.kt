package com.linh.perfin.data.repository.split.mapper

import com.linh.perfin.common.utils.toInstant
import com.linh.perfin.domain.model.split.Participant
import com.perfin.features.expensetracking.ParticipantEntity
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun Participant.toParticipantEntity() = ParticipantEntity(
    participant_id = id,
    name = name,
    email = email,
    phone = phone,
    avatar_url = avatarUrl,
    created_at = createdAt.toEpochMilliseconds(),
    updated_at = updatedAt.toEpochMilliseconds()
)

@OptIn(ExperimentalTime::class)
fun ParticipantEntity.toParticipant() = Participant(
    id = participant_id,
    name = name,
    email = email,
    phone = phone,
    avatarUrl = avatar_url,
    createdAt = created_at.toInstant(),
    updatedAt = updated_at.toInstant()
)
