package com.linh.perfin.presentation.split

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.linh.perfin.domain.model.split.Participant

sealed class SplitTransactionUiState {
    data object Loading : SplitTransactionUiState()

    data class Editing(
        val transactionId: String,
        val transactionAmount: BigDecimal,
        val formState: SplitFormState,
        val participants: List<Participant>,
        val isSubmitting: Boolean = false,
        val error: String? = null
    ) : SplitTransactionUiState()

    data class Error(val message: String) : SplitTransactionUiState()

    data object SubmitSuccess : SplitTransactionUiState()
}
