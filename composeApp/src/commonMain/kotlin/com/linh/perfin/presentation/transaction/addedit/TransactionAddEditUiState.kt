package com.linh.perfin.presentation.transaction.addedit

import com.linh.perfin.domain.model.account.Account
import com.linh.perfin.domain.model.category.Category

sealed class TransactionAddEditUiState {
    data object Loading : TransactionAddEditUiState()

    data class Editing(
        val formState: TransactionFormState,
        val accounts: List<Account>,
        val categories: List<Category>,
        val isSubmitting: Boolean = false,
        val error: String? = null
    ) : TransactionAddEditUiState()

    data class Error(val message: String) : TransactionAddEditUiState()

    data object SubmitSuccess : TransactionAddEditUiState()
}
