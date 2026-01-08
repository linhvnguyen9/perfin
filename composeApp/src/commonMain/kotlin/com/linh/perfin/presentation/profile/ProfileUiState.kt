package com.linh.perfin.presentation.profile

sealed class ProfileUiState {
    data object Loading : ProfileUiState()
    data class Success(val userName: String = "User") : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}
