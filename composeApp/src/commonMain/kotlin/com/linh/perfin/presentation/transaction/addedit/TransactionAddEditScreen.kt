package com.linh.perfin.presentation.transaction.addedit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.linh.perfin.presentation.transaction.addedit.components.AccountSelector
import com.linh.perfin.presentation.transaction.addedit.components.AmountInput
import com.linh.perfin.presentation.transaction.addedit.components.CategorySelector
import com.linh.perfin.presentation.transaction.addedit.components.DateTimePickerField
import com.linh.perfin.presentation.transaction.addedit.components.DescriptionInput
import com.linh.perfin.presentation.transaction.addedit.components.FormActionButtons
import com.linh.perfin.presentation.transaction.addedit.components.LocationInput
import com.linh.perfin.presentation.transaction.addedit.components.NotesInput
import com.linh.perfin.presentation.transaction.addedit.components.ReconciledToggle
import com.linh.perfin.presentation.transaction.addedit.components.TransactionTypeToggle
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import perfin.composeapp.generated.resources.Res
import perfin.composeapp.generated.resources.arrow_back_24px
import perfin.composeapp.generated.resources.delete_24px

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionAddEditScreen(
    transactionId: String?,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TransactionAddEditViewModel = koinViewModel(
        parameters = { parametersOf(transactionId) }
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showUnsavedChangesDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    // Handle navigation on success
    LaunchedEffect(uiState) {
        if (uiState is TransactionAddEditUiState.SubmitSuccess) {
            onNavigateBack()
        }
    }

    // Handle error snackbar
    LaunchedEffect(uiState) {
        val currentState = uiState
        if (currentState is TransactionAddEditUiState.Editing && currentState.error != null) {
            snackbarHostState.showSnackbar(currentState.error)
            viewModel.clearError()
        }
    }

    val isEditMode = transactionId != null

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = if (isEditMode) "Edit Transaction" else "Add Transaction",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (viewModel.canNavigateAway()) {
                            onNavigateBack()
                        } else {
                            showUnsavedChangesDialog = true
                        }
                    }) {
                        Icon(
                            painter = painterResource(Res.drawable.arrow_back_24px),
                            contentDescription = "Navigate back"
                        )
                    }
                },
                actions = {
                    if (isEditMode) {
                        IconButton(onClick = { showDeleteConfirmDialog = true }) {
                            Icon(
                                painter = painterResource(Res.drawable.delete_24px),
                                contentDescription = "Delete transaction",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { paddingValues ->
        when (val state = uiState) {
            is TransactionAddEditUiState.Loading -> {
                LoadingState(modifier = Modifier.padding(paddingValues))
            }

            is TransactionAddEditUiState.Editing -> {
                TransactionForm(
                    formState = state.formState,
                    accounts = state.accounts,
                    categories = state.categories,
                    isSubmitting = state.isSubmitting,
                    isEditMode = isEditMode,
                    viewModel = viewModel,
                    modifier = Modifier.padding(paddingValues)
                )
            }

            is TransactionAddEditUiState.Error -> {
                ErrorState(
                    message = state.message,
                    modifier = Modifier.padding(paddingValues)
                )
            }

            is TransactionAddEditUiState.SubmitSuccess -> {
                // Navigation handled by LaunchedEffect
            }
        }
    }

    // Unsaved changes dialog
    if (showUnsavedChangesDialog) {
        AlertDialog(
            onDismissRequest = { showUnsavedChangesDialog = false },
            title = { Text("Discard changes?") },
            text = { Text("You have unsaved changes. Are you sure you want to leave?") },
            confirmButton = {
                TextButton(onClick = {
                    showUnsavedChangesDialog = false
                    onNavigateBack()
                }) {
                    Text("Discard")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUnsavedChangesDialog = false }) {
                    Text("Keep Editing")
                }
            }
        )
    }

    // Delete confirmation dialog
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Delete transaction?") },
            text = { Text("This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmDialog = false
                        viewModel.deleteTransaction()
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun TransactionForm(
    formState: TransactionFormState,
    accounts: List<com.linh.perfin.domain.model.account.Account>,
    categories: List<com.linh.perfin.domain.model.category.Category>,
    isSubmitting: Boolean,
    isEditMode: Boolean,
    viewModel: TransactionAddEditViewModel,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Core Fields Section
        item {
            Text(
                text = "Transaction Details",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        item {
            TransactionTypeToggle(
                selectedType = formState.transactionType,
                onTypeSelected = viewModel::updateTransactionType,
                enabled = !isSubmitting
            )
        }

        item {
            AccountSelector(
                selectedAccountId = formState.accountId,
                accounts = accounts,
                onAccountSelected = {
                    viewModel.updateAccount(it)
                    viewModel.markFieldAsTouched(TransactionFormState.FormField.ACCOUNT)
                },
                isError = formState.touchedFields.contains(TransactionFormState.FormField.ACCOUNT) &&
                        formState.errors.containsKey(TransactionFormState.FormField.ACCOUNT),
                errorMessage = formState.errors[TransactionFormState.FormField.ACCOUNT],
                enabled = !isSubmitting
            )
        }

        item {
            AmountInput(
                amount = formState.amount,
                onAmountChange = viewModel::updateAmount,
                onBlur = {
                    viewModel.markFieldAsTouched(TransactionFormState.FormField.AMOUNT)
                },
                isError = formState.touchedFields.contains(TransactionFormState.FormField.AMOUNT) &&
                        formState.errors.containsKey(TransactionFormState.FormField.AMOUNT),
                errorMessage = formState.errors[TransactionFormState.FormField.AMOUNT],
                enabled = !isSubmitting
            )
        }

        item {
            DescriptionInput(
                description = formState.description,
                onDescriptionChange = viewModel::updateDescription,
                isError = formState.touchedFields.contains(TransactionFormState.FormField.DESCRIPTION) &&
                        formState.errors.containsKey(TransactionFormState.FormField.DESCRIPTION),
                errorMessage = formState.errors[TransactionFormState.FormField.DESCRIPTION],
                enabled = !isSubmitting
            )
        }

        item {
            DateTimePickerField(
                selectedDateTime = formState.dateTime,
                onDateTimeSelected = viewModel::updateDateTime,
                enabled = !isSubmitting
            )
        }

        // Additional Details Section
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Additional Details",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        item {
            CategorySelector(
                selectedCategoryId = formState.categoryId,
                categories = categories,
                onCategorySelected = viewModel::updateCategory,
                enabled = !isSubmitting
            )
        }

        item {
            NotesInput(
                notes = formState.notes,
                onNotesChange = viewModel::updateNotes,
                isError = formState.touchedFields.contains(TransactionFormState.FormField.NOTES) &&
                        formState.errors.containsKey(TransactionFormState.FormField.NOTES),
                errorMessage = formState.errors[TransactionFormState.FormField.NOTES],
                enabled = !isSubmitting
            )
        }

        item {
            LocationInput(
                location = formState.location,
                onLocationChange = viewModel::updateLocation,
                isError = formState.touchedFields.contains(TransactionFormState.FormField.LOCATION) &&
                        formState.errors.containsKey(TransactionFormState.FormField.LOCATION),
                errorMessage = formState.errors[TransactionFormState.FormField.LOCATION],
                enabled = !isSubmitting
            )
        }

        item {
            ReconciledToggle(
                isReconciled = formState.isReconciled,
                onReconciledChange = { viewModel.toggleReconciled() },
                enabled = !isSubmitting
            )
        }

        // Action Buttons
        item {
            FormActionButtons(
                onSave = viewModel::submitForm,
                onCancel = { /* Handled by back button */ },
                isEditMode = isEditMode,
                isSaving = isSubmitting,
                canSave = formState.hasRequiredFields() && formState.isValid()
            )
        }
    }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorState(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "Error",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.error
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}
