package com.linh.perfin.presentation.account.addedit

import com.linh.perfin.domain.model.BankInVietnam

data class AccountFormState(
    val accountId: String? = null,
    val name: String = "",
    val bank: BankInVietnam? = null,
    val errors: Map<FormField, String> = emptyMap(),
    val touchedFields: Set<FormField> = emptySet(),
    val isDirty: Boolean = false
) {
    enum class FormField {
        NAME,
        BANK
    }

    fun isValid(): Boolean = errors.isEmpty()

    fun hasRequiredFields(): Boolean = name.isNotBlank()
}
