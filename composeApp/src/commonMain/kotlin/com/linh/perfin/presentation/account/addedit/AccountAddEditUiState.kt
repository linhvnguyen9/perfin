package com.linh.perfin.presentation.account.addedit

sealed class AccountAddEditUiState {
    data object Loading : AccountAddEditUiState()

    data class Editing(
        val formState: AccountFormState,
        val isSubmitting: Boolean = false,
        val error: String? = null
    ) : AccountAddEditUiState()

    data class Error(val message: String) : AccountAddEditUiState()

    data object SubmitSuccess : AccountAddEditUiState()
}
