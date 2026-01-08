package com.linh.perfin.presentation.transaction.addedit.models

import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.linh.perfin.domain.model.transaction.Transaction
import com.linh.perfin.domain.model.transaction.TransactionType
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class TransactionFormData(
    val transactionId: String? = null,
    val accountId: String,
    val categoryId: String?,
    val amount: String,
    val description: String,
    val transactionType: TransactionType,
    val dateTime: Instant,
    val notes: String,
    val location: String,
    val isReconciled: Boolean
) {
    @OptIn(ExperimentalUuidApi::class)
    fun toTransaction(originalCreatedAt: Instant? = null): Transaction {
        // Handle sign based on transaction type
        val amountValue = amount.toDoubleOrNull() ?: 0.0
        val signedAmount = when (transactionType) {
            TransactionType.INCOME -> amountValue
            TransactionType.EXPENSE -> -amountValue
            TransactionType.TRANSFER -> amountValue
        }

        return Transaction(
            id = transactionId ?: Uuid.random().toString(),
            accountId = accountId,
            amount = signedAmount.toBigDecimal(),
            description = description,
            date = dateTime,
            notes = notes,
            location = location,
            createdAt = originalCreatedAt ?: Clock.System.now(),
            updatedAt = Clock.System.now(),
            isReconciled = isReconciled
        )
    }

    companion object {
        fun fromTransaction(transaction: Transaction): TransactionFormData {
            val absAmount = transaction.amount.abs()
            val type = when {
                transaction.amount > 0.0.toBigDecimal() -> TransactionType.INCOME
                transaction.amount < 0.0.toBigDecimal() -> TransactionType.EXPENSE
                else -> TransactionType.EXPENSE
            }

            return TransactionFormData(
                transactionId = transaction.id,
                accountId = transaction.accountId,
                categoryId = null, // TODO: Add category relationship
                amount = absAmount.toString(),
                description = transaction.description,
                transactionType = type,
                dateTime = transaction.date,
                notes = transaction.notes,
                location = transaction.location,
                isReconciled = transaction.isReconciled
            )
        }

        fun empty(): TransactionFormData {
            return TransactionFormData(
                transactionId = null,
                accountId = "",
                categoryId = null,
                amount = "",
                description = "",
                transactionType = TransactionType.EXPENSE,
                dateTime = Clock.System.now(),
                notes = "",
                location = "",
                isReconciled = false
            )
        }
    }
}
