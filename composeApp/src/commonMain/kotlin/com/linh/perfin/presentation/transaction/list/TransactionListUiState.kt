package com.linh.perfin.presentation.transaction.list

import com.linh.perfin.domain.model.transaction.TransactionWithDetails

sealed class TransactionListUiState {
    data object Loading : TransactionListUiState()
    data class Success(val transactions: List<TransactionWithDetails>) : TransactionListUiState()
    data class Error(val message: String) : TransactionListUiState()
}
