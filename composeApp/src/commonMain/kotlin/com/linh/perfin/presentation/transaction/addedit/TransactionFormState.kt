package com.linh.perfin.presentation.transaction.addedit

import com.linh.perfin.domain.model.transaction.TransactionType
import kotlin.time.Clock
import kotlin.time.Instant

data class TransactionFormState(
    val transactionId: String? = null,  // null = ADD mode, non-null = EDIT mode
    val accountId: String = "",
    val categoryId: String? = null,
    val amount: String = "",
    val description: String = "",
    val transactionType: TransactionType = TransactionType.EXPENSE,
    val dateTime: Instant = Clock.System.now(),
    val notes: String = "",
    val location: String = "",
    val isReconciled: Boolean = false,

    // Validation state
    val errors: Map<FormField, String> = emptyMap(),
    val touchedFields: Set<FormField> = emptySet(),
    val isDirty: Boolean = false
) {
    enum class FormField {
        ACCOUNT,
        CATEGORY,
        AMOUNT,
        DESCRIPTION,
        TRANSACTION_TYPE,
        DATE_TIME,
        NOTES,
        LOCATION,
        RECONCILED
    }

    fun isValid(): Boolean = errors.isEmpty()

    fun hasRequiredFields(): Boolean {
        return accountId.isNotBlank() &&
               amount.isNotBlank() &&
               description.isNotBlank()
    }
}
