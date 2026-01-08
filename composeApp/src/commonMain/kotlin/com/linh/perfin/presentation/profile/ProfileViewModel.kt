package com.linh.perfin.presentation.profile

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Success())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
}
