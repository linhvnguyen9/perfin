package com.linh.perfin.presentation.transaction.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linh.perfin.domain.usecase.transaction.GetTransactionsWithDetailsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class TransactionListViewModel(
    private val getTransactionsWithDetailsUseCase: GetTransactionsWithDetailsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<TransactionListUiState>(TransactionListUiState.Loading)
    val uiState: StateFlow<TransactionListUiState> = _uiState.asStateFlow()

    init {
        loadTransactions()
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            _uiState.value = TransactionListUiState.Loading
            getTransactionsWithDetailsUseCase()
                .catch { exception ->
                    _uiState.value = TransactionListUiState.Error(
                        exception.message ?: "An unknown error occurred"
                    )
                }
                .collect { transactions ->
                    val sortedTransactions = transactions.sortedByDescending { it.transaction.date }
                    _uiState.value = TransactionListUiState.Success(sortedTransactions)
                }
        }
    }
}
