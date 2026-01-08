package com.linh.perfin.presentation.account.list

import com.linh.perfin.domain.model.account.Account

sealed class AccountListUiState {
    data object Loading : AccountListUiState()
    data class Success(val accounts: List<Account>) : AccountListUiState()
    data class Error(val message: String) : AccountListUiState()
}