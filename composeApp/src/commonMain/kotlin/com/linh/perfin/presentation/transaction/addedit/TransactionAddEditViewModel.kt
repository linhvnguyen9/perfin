package com.linh.perfin.presentation.transaction.addedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linh.perfin.domain.model.transaction.TransactionType
import com.linh.perfin.domain.repository.category.CategoryRepository
import com.linh.perfin.domain.repository.transaction.TransactionRepository
import com.linh.perfin.domain.usecase.account.GetAllAccountsUseCase
import com.linh.perfin.domain.usecase.transaction.CreateTransactionUseCase
import com.linh.perfin.presentation.transaction.addedit.models.TransactionFormData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant

class TransactionAddEditViewModel(
    private val transactionId: String?,
    private val createTransactionUseCase: CreateTransactionUseCase,
    private val getAllAccountsUseCase: GetAllAccountsUseCase,
    private val categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository,
    private val validator: TransactionFormValidator
) : ViewModel() {

    private val _uiState = MutableStateFlow<TransactionAddEditUiState>(TransactionAddEditUiState.Loading)
    val uiState: StateFlow<TransactionAddEditUiState> = _uiState.asStateFlow()

    private var originalCreatedAt: Instant? = null

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            try {
                // Load accounts and categories in parallel
                combine(
                    flowOf(getAllAccountsUseCase()),
                    categoryRepository.getAllCategories()
                ) { accounts, categories ->
                    Pair(accounts, categories)
                }
                    .catch { exception ->
                        _uiState.value = TransactionAddEditUiState.Error(
                            exception.message ?: "Failed to load data"
                        )
                    }
                    .collect { (accounts, categories) ->
                        if (transactionId != null) {
                            // EDIT mode: load transaction
                            loadTransactionForEdit(accounts, categories, transactionId)
                        } else {
                            // ADD mode: start with empty form
                            _uiState.value = TransactionAddEditUiState.Editing(
                                formState = TransactionFormState(),
                                accounts = accounts,
                                categories = categories
                            )
                        }
                    }
            } catch (e: Exception) {
                _uiState.value = TransactionAddEditUiState.Error(
                    e.message ?: "Failed to load data"
                )
            }
        }
    }

    private suspend fun loadTransactionForEdit(
        accounts: List<com.linh.perfin.domain.model.account.Account>,
        categories: List<com.linh.perfin.domain.model.category.Category>,
        id: String
    ) {
        try {
            val transaction = transactionRepository.getTransactionById(id)
            if (transaction != null) {
                originalCreatedAt = transaction.createdAt
                val formData = TransactionFormData.fromTransaction(transaction)
                _uiState.value = TransactionAddEditUiState.Editing(
                    formState = TransactionFormState(
                        transactionId = formData.transactionId,
                        accountId = formData.accountId,
                        categoryId = formData.categoryId,
                        amount = formData.amount,
                        description = formData.description,
                        transactionType = formData.transactionType,
                        dateTime = formData.dateTime,
                        notes = formData.notes,
                        location = formData.location,
                        isReconciled = formData.isReconciled
                    ),
                    accounts = accounts,
                    categories = categories
                )
            } else {
                _uiState.value = TransactionAddEditUiState.Error("Transaction not found")
            }
        } catch (e: Exception) {
            _uiState.value = TransactionAddEditUiState.Error(
                e.message ?: "Failed to load transaction"
            )
        }
    }

    fun updateAccount(accountId: String) {
        updateFormState { it.copy(accountId = accountId, isDirty = true) }
        validateField(TransactionFormState.FormField.ACCOUNT)
    }

    fun updateCategory(categoryId: String?) {
        updateFormState { it.copy(categoryId = categoryId, isDirty = true) }
    }

    fun updateAmount(amount: String) {
        // Filter to allow only numbers and decimal point
        val filteredAmount = amount.filter { it.isDigit() || it == '.' }
        updateFormState { it.copy(amount = filteredAmount, isDirty = true) }
    }

    fun updateDescription(description: String) {
        updateFormState { it.copy(description = description, isDirty = true) }
    }

    fun updateTransactionType(type: TransactionType) {
        updateFormState { it.copy(transactionType = type, isDirty = true) }
    }

    fun updateDateTime(dateTime: Instant) {
        updateFormState { it.copy(dateTime = dateTime, isDirty = true) }
    }

    fun updateNotes(notes: String) {
        updateFormState { it.copy(notes = notes, isDirty = true) }
    }

    fun updateLocation(location: String) {
        updateFormState { it.copy(location = location, isDirty = true) }
    }

    fun toggleReconciled() {
        updateFormState { it.copy(isReconciled = !it.isReconciled, isDirty = true) }
    }

    fun markFieldAsTouched(field: TransactionFormState.FormField) {
        updateFormState {
            it.copy(touchedFields = it.touchedFields + field)
        }
        validateField(field)
    }

    private fun validateField(field: TransactionFormState.FormField) {
        val currentState = (_uiState.value as? TransactionAddEditUiState.Editing) ?: return
        val formState = currentState.formState

        val error = when (field) {
            TransactionFormState.FormField.ACCOUNT -> validator.validateAccount(formState.accountId)
            TransactionFormState.FormField.AMOUNT -> validator.validateAmount(formState.amount)
            TransactionFormState.FormField.DESCRIPTION -> validator.validateDescription(formState.description)
            TransactionFormState.FormField.NOTES -> validator.validateNotes(formState.notes)
            TransactionFormState.FormField.LOCATION -> validator.validateLocation(formState.location)
            else -> null
        }

        val updatedErrors = if (error != null) {
            formState.errors + (field to error)
        } else {
            formState.errors - field
        }

        updateFormState { it.copy(errors = updatedErrors) }
    }

    private fun validateAllFields(): Boolean {
        val currentState = (_uiState.value as? TransactionAddEditUiState.Editing) ?: return false
        val formState = currentState.formState

        val errors = validator.validateForm(formState)

        updateFormState {
            it.copy(
                errors = errors,
                touchedFields = TransactionFormState.FormField.entries.toSet()
            )
        }

        return errors.isEmpty()
    }

    fun submitForm() {
        val currentState = (_uiState.value as? TransactionAddEditUiState.Editing) ?: return

        if (!validateAllFields()) {
            return
        }

        val formState = currentState.formState

        viewModelScope.launch {
            try {
                _uiState.value = currentState.copy(isSubmitting = true)

                val formData = TransactionFormData(
                    transactionId = formState.transactionId,
                    accountId = formState.accountId,
                    categoryId = formState.categoryId,
                    amount = formState.amount,
                    description = formState.description,
                    transactionType = formState.transactionType,
                    dateTime = formState.dateTime,
                    notes = formState.notes,
                    location = formState.location,
                    isReconciled = formState.isReconciled
                )

                val transaction = formData.toTransaction(originalCreatedAt)

                if (formState.transactionId != null) {
                    // Update existing transaction
                    transactionRepository.updateTransaction(transaction)
                } else {
                    // Create new transaction
                    createTransactionUseCase(transaction)
                }

                _uiState.value = TransactionAddEditUiState.SubmitSuccess
            } catch (e: Exception) {
                _uiState.value = currentState.copy(
                    isSubmitting = false,
                    error = e.message ?: "Failed to save transaction"
                )
            }
        }
    }

    fun deleteTransaction() {
        val currentState = (_uiState.value as? TransactionAddEditUiState.Editing) ?: return
        val transactionId = currentState.formState.transactionId ?: return

        viewModelScope.launch {
            try {
                _uiState.value = currentState.copy(isSubmitting = true)
                transactionRepository.deleteTransaction(transactionId)
                _uiState.value = TransactionAddEditUiState.SubmitSuccess
            } catch (e: Exception) {
                _uiState.value = currentState.copy(
                    isSubmitting = false,
                    error = e.message ?: "Failed to delete transaction"
                )
            }
        }
    }

    fun canNavigateAway(): Boolean {
        val currentState = (_uiState.value as? TransactionAddEditUiState.Editing) ?: return true
        return !currentState.formState.isDirty
    }

    fun clearError() {
        val currentState = (_uiState.value as? TransactionAddEditUiState.Editing) ?: return
        _uiState.value = currentState.copy(error = null)
    }

    private fun updateFormState(update: (TransactionFormState) -> TransactionFormState) {
        val currentState = (_uiState.value as? TransactionAddEditUiState.Editing) ?: return
        _uiState.value = currentState.copy(formState = update(currentState.formState))
    }
}
