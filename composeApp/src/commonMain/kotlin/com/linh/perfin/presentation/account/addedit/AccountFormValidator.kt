package com.linh.perfin.presentation.account.addedit

import com.linh.perfin.domain.model.BankInVietnam

class AccountFormValidator {

    fun validateName(name: String): String? {
        return when {
            name.isBlank() -> "Account name cannot be empty"
            name.length > 255 -> "Account name is too long (max 255 characters)"
            else -> null
        }
    }

    fun validateBank(bank: BankInVietnam?): String? {
        // Bank is optional, no validation needed
        return null
    }

    fun validateForm(formState: AccountFormState): Map<AccountFormState.FormField, String> {
        val errors = mutableMapOf<AccountFormState.FormField, String>()

        validateName(formState.name)?.let {
            errors[AccountFormState.FormField.NAME] = it
        }

        return errors
    }
}
