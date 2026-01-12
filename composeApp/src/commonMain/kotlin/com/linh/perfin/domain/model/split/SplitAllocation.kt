package com.linh.perfin.domain.model.split

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
data class SplitAllocation(
    val id: String = Uuid.random().toString(),
    val splitTransactionId: String,
    val participant: Participant,
    val allocationValue: BigDecimal,
    val calculatedAmount: BigDecimal,
    val createdAt: Instant,
    val updatedAt: Instant
)
