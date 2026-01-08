package com.linh.perfin.presentation.transaction.addedit

class TransactionFormValidator {

    fun validateAccount(accountId: String): String? {
        return if (accountId.isBlank()) {
            "Please select an account"
        } else null
    }

    fun validateAmount(amount: String): String? {
        if (amount.isBlank()) {
            return "Amount is required"
        }

        val amountValue = amount.toDoubleOrNull()
        if (amountValue == null) {
            return "Invalid amount format"
        }

        if (amountValue <= 0) {
            return "Amount must be greater than 0"
        }

        // Check decimal places (max 2)
        val decimalPart = amount.substringAfter('.', "")
        if (decimalPart.length > 2) {
            return "Amount can have at most 2 decimal places"
        }

        return null
    }

    fun validateDescription(description: String): String? {
        return when {
            description.isBlank() -> "Description cannot be empty"
            description.length > 255 -> "Description is too long (max 255 characters)"
            else -> null
        }
    }

    fun validateNotes(notes: String): String? {
        return if (notes.length > 1000) {
            "Notes are too long (max 1000 characters)"
        } else null
    }

    fun validateLocation(location: String): String? {
        return if (location.length > 255) {
            "Location is too long (max 255 characters)"
        } else null
    }

    fun validateForm(formState: TransactionFormState): Map<TransactionFormState.FormField, String> {
        val errors = mutableMapOf<TransactionFormState.FormField, String>()

        validateAccount(formState.accountId)?.let {
            errors[TransactionFormState.FormField.ACCOUNT] = it
        }

        validateAmount(formState.amount)?.let {
            errors[TransactionFormState.FormField.AMOUNT] = it
        }

        validateDescription(formState.description)?.let {
            errors[TransactionFormState.FormField.DESCRIPTION] = it
        }

        validateNotes(formState.notes)?.let {
            errors[TransactionFormState.FormField.NOTES] = it
        }

        validateLocation(formState.location)?.let {
            errors[TransactionFormState.FormField.LOCATION] = it
        }

        return errors
    }
}
