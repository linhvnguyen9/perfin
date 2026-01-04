package com.linh.perfin.data.repository.transaction.mapper

import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.linh.perfin.common.utils.toBoolean
import com.linh.perfin.common.utils.toInstant
import com.linh.perfin.common.utils.toLong
import com.linh.perfin.domain.model.transaction.Transaction
import com.perfin.features.expensetracking.TransactionsEntity
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun Transaction.toTransactionEntity() = TransactionsEntity(
    transaction_id = id,
    account_id = accountId,
    amount = amount.toStringExpanded(),
    notes = notes,
    location = location,
    created_at = createdAt.toEpochMilliseconds(),
    updated_at = updatedAt.toEpochMilliseconds(),
    title = description,
    is_reconciled = isReconciled.toLong(),
    date = date.toEpochMilliseconds()
)

@OptIn(ExperimentalTime::class)
fun TransactionsEntity.toTransaction() = Transaction(
    id = transaction_id,
    accountId = account_id,
    amount = amount.toBigDecimal(),
    description = title,
    date = date.toInstant(),
    notes = notes.orEmpty(),
    location = location.orEmpty(),
    createdAt = created_at.toInstant(),
    updatedAt = updated_at.toInstant(),
    isReconciled = is_reconciled.toBoolean()
)