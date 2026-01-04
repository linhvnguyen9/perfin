package com.linh.perfin.domain.model.transaction

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
data class Transaction(
    val id: String = Uuid.random().toString(),
    val accountId: String,
    val amount: BigDecimal,
    val description: String,
    val date: Instant,
    val notes: String,
    val location: String,
    val createdAt: Instant,
    val updatedAt: Instant,
    val isReconciled: Boolean,
)