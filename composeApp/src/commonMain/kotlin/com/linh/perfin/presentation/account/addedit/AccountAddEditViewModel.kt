package com.linh.perfin.presentation.account.addedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linh.perfin.domain.model.BankInVietnam
import com.linh.perfin.domain.usecase.account.AccountInUseException
import com.linh.perfin.domain.usecase.account.CreateAccountUseCase
import com.linh.perfin.domain.usecase.account.DeleteAccountUseCase
import com.linh.perfin.domain.usecase.account.GetAccountByIdUseCase
import com.linh.perfin.domain.usecase.account.UpdateAccountUseCase
import com.linh.perfin.presentation.account.addedit.models.AccountFormData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AccountAddEditViewModel(
    private val accountId: String?,
    private val createAccountUseCase: CreateAccountUseCase,
    private val getAccountByIdUseCase: GetAccountByIdUseCase,
    private val updateAccountUseCase: UpdateAccountUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val validator: AccountFormValidator
) : ViewModel() {

    private val _uiState = MutableStateFlow<AccountAddEditUiState>(AccountAddEditUiState.Loading)
    val uiState: StateFlow<AccountAddEditUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            try {
                if (accountId != null) {
                    // EDIT mode: load account
                    loadAccountForEdit(accountId)
                } else {
                    // ADD mode: start with empty form
                    _uiState.value = AccountAddEditUiState.Editing(
                        formState = AccountFormState()
                    )
                }
            } catch (e: Exception) {
                _uiState.value = AccountAddEditUiState.Error(
                    e.message ?: "Failed to load account"
                )
            }
        }
    }

    private suspend fun loadAccountForEdit(id: String) {
        try {
            val account = getAccountByIdUseCase(id)
            if (account != null) {
                val formData = AccountFormData.fromAccount(account)
                _uiState.value = AccountAddEditUiState.Editing(
                    formState = AccountFormState(
                        accountId = formData.accountId,
                        name = formData.name,
                        bank = formData.bank
                    )
                )
            } else {
                _uiState.value = AccountAddEditUiState.Error("Account not found")
            }
        } catch (e: Exception) {
            _uiState.value = AccountAddEditUiState.Error(
                e.message ?: "Failed to load account"
            )
        }
    }

    fun updateName(name: String) {
        updateFormState { it.copy(name = name, isDirty = true) }
    }

    fun updateBank(bank: BankInVietnam?) {
        updateFormState { it.copy(bank = bank, isDirty = true) }
    }

    fun markFieldAsTouched(field: AccountFormState.FormField) {
        updateFormState {
            it.copy(touchedFields = it.touchedFields + field)
        }
        validateField(field)
    }

    private fun validateField(field: AccountFormState.FormField) {
        val currentState = (_uiState.value as? AccountAddEditUiState.Editing) ?: return
        val formState = currentState.formState

        val error = when (field) {
            AccountFormState.FormField.NAME -> validator.validateName(formState.name)
            AccountFormState.FormField.BANK -> validator.validateBank(formState.bank)
        }

        val updatedErrors = if (error != null) {
            formState.errors + (field to error)
        } else {
            formState.errors - field
        }

        updateFormState { it.copy(errors = updatedErrors) }
    }

    private fun validateAllFields(): Boolean {
        val currentState = (_uiState.value as? AccountAddEditUiState.Editing) ?: return false
        val formState = currentState.formState

        val errors = validator.validateForm(formState)

        updateFormState {
            it.copy(
                errors = errors,
                touchedFields = AccountFormState.FormField.entries.toSet()
            )
        }

        return errors.isEmpty()
    }

    fun submitForm() {
        val currentState = (_uiState.value as? AccountAddEditUiState.Editing) ?: return

        if (!validateAllFields()) {
            return
        }

        val formState = currentState.formState

        viewModelScope.launch {
            try {
                _uiState.value = currentState.copy(isSubmitting = true)

                val formData = AccountFormData(
                    accountId = formState.accountId,
                    name = formState.name,
                    bank = formState.bank
                )

                val account = formData.toAccount()

                if (formState.accountId != null) {
                    // Update existing account
                    updateAccountUseCase(account)
                } else {
                    // Create new account
                    createAccountUseCase(account)
                }

                _uiState.value = AccountAddEditUiState.SubmitSuccess
            } catch (e: Exception) {
                _uiState.value = currentState.copy(
                    isSubmitting = false,
                    error = e.message ?: "Failed to save account"
                )
            }
        }
    }

    fun deleteAccount() {
        val currentState = (_uiState.value as? AccountAddEditUiState.Editing) ?: return
        val accountId = currentState.formState.accountId ?: return

        viewModelScope.launch {
            try {
                _uiState.value = currentState.copy(isSubmitting = true)
                deleteAccountUseCase(accountId)
                _uiState.value = AccountAddEditUiState.SubmitSuccess
            } catch (e: AccountInUseException) {
                _uiState.value = currentState.copy(
                    isSubmitting = false,
                    error = "Cannot delete account with existing transactions"
                )
            } catch (e: Exception) {
                _uiState.value = currentState.copy(
                    isSubmitting = false,
                    error = e.message ?: "Failed to delete account"
                )
            }
        }
    }

    fun canNavigateAway(): Boolean {
        val currentState = (_uiState.value as? AccountAddEditUiState.Editing) ?: return true
        return !currentState.formState.isDirty
    }

    fun clearError() {
        val currentState = (_uiState.value as? AccountAddEditUiState.Editing) ?: return
        _uiState.value = currentState.copy(error = null)
    }

    private fun updateFormState(update: (AccountFormState) -> AccountFormState) {
        val currentState = (_uiState.value as? AccountAddEditUiState.Editing) ?: return
        _uiState.value = currentState.copy(formState = update(currentState.formState))
    }
}
