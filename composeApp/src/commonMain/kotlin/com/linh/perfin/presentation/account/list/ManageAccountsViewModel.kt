package com.linh.perfin.presentation.account.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linh.perfin.domain.usecase.account.GetAllAccountsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ManageAccountsViewModel(
    private val getAllAccountsUseCase: GetAllAccountsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AccountListUiState>(AccountListUiState.Loading)
    val uiState: StateFlow<AccountListUiState> = _uiState.asStateFlow()

    init {
        observeAccounts()
    }

    private fun observeAccounts() {
        viewModelScope.launch {
            try {
                getAllAccountsUseCase.observe().collect { accounts ->
                    _uiState.value = AccountListUiState.Success(accounts)
                }
            } catch (e: Exception) {
                _uiState.value = AccountListUiState.Error(
                    e.message ?: "Failed to load accounts"
                )
            }
        }
    }
}