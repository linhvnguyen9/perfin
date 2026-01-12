package com.linh.perfin.domain.model.split

import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
data class Participant(
    val id: String = Uuid.random().toString(),
    val name: String,
    val email: String? = null,
    val phone: String? = null,
    val avatarUrl: String? = null,
    val createdAt: Instant,
    val updatedAt: Instant
)
