package com.linh.perfin.presentation.account.addedit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.linh.perfin.presentation.account.addedit.components.AccountNameInput
import com.linh.perfin.presentation.account.addedit.components.AccountNumberInput
import com.linh.perfin.presentation.account.addedit.components.BankSelector
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import perfin.composeapp.generated.resources.Res
import perfin.composeapp.generated.resources.arrow_back_24px
import perfin.composeapp.generated.resources.delete_24px

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountAddEditScreen(
    accountId: String?,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AccountAddEditViewModel = koinViewModel(
        parameters = { parametersOf(accountId) }
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showUnsavedChangesDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    // Handle navigation on success
    LaunchedEffect(uiState) {
        if (uiState is AccountAddEditUiState.SubmitSuccess) {
            onNavigateBack()
        }
    }

    // Handle error snackbar
    LaunchedEffect(uiState) {
        val currentState = uiState
        if (currentState is AccountAddEditUiState.Editing && currentState.error != null) {
            snackbarHostState.showSnackbar(currentState.error)
            viewModel.clearError()
        }
    }

    val isEditMode = accountId != null

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = if (isEditMode) "Edit Account" else "Add Account",
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
                                contentDescription = "Delete account",
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
            is AccountAddEditUiState.Loading -> {
                LoadingState(modifier = Modifier.padding(paddingValues))
            }

            is AccountAddEditUiState.Editing -> {
                AccountForm(
                    formState = state.formState,
                    isSubmitting = state.isSubmitting,
                    isEditMode = isEditMode,
                    viewModel = viewModel,
                    onNavigateBack = onNavigateBack,
                    onShowUnsavedChangesDialog = { showUnsavedChangesDialog = true },
                    modifier = Modifier.padding(paddingValues)
                )
            }

            is AccountAddEditUiState.Error -> {
                ErrorState(
                    message = state.message,
                    modifier = Modifier.padding(paddingValues)
                )
            }

            is AccountAddEditUiState.SubmitSuccess -> {
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
            title = { Text("Delete account?") },
            text = { Text("This action cannot be undone. You cannot delete an account with existing transactions.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmDialog = false
                        viewModel.deleteAccount()
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
private fun AccountForm(
    formState: AccountFormState,
    isSubmitting: Boolean,
    isEditMode: Boolean,
    viewModel: AccountAddEditViewModel,
    onNavigateBack: () -> Unit,
    onShowUnsavedChangesDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Account Details Section
        item {
            Text(
                text = "Account Details",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        item {
            AccountNameInput(
                name = formState.name,
                onNameChange = {
                    viewModel.updateName(it)
                    viewModel.markFieldAsTouched(AccountFormState.FormField.NAME)
                },
                isError = formState.touchedFields.contains(AccountFormState.FormField.NAME) &&
                        formState.errors.containsKey(AccountFormState.FormField.NAME),
                errorMessage = formState.errors[AccountFormState.FormField.NAME],
                enabled = !isSubmitting
            )
        }

        item {
            AccountNumberInput(
                accountNumber = formState.accountNumber,
                onAccountNumberChange = {
                    viewModel.updateAccountNumber(it)
                    viewModel.markFieldAsTouched(AccountFormState.FormField.ACCOUNT_NUMBER)
                },
                isError = formState.touchedFields.contains(AccountFormState.FormField.ACCOUNT_NUMBER) &&
                        formState.errors.containsKey(AccountFormState.FormField.ACCOUNT_NUMBER),
                errorMessage = formState.errors[AccountFormState.FormField.ACCOUNT_NUMBER],
                enabled = !isSubmitting
            )
        }

        item {
            BankSelector(
                selectedBank = formState.bank,
                onBankSelected = {
                    viewModel.updateBank(it)
                    viewModel.markFieldAsTouched(AccountFormState.FormField.BANK)
                },
                enabled = !isSubmitting
            )
        }

        // Action Buttons
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        if (viewModel.canNavigateAway()) {
                            onNavigateBack()
                        } else {
                            onShowUnsavedChangesDialog()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !isSubmitting
                ) {
                    Text("Cancel")
                }

                Button(
                    onClick = viewModel::submitForm,
                    modifier = Modifier.weight(1f),
                    enabled = formState.hasRequiredFields() && formState.isValid() && !isSubmitting
                ) {
                    Text(if (isSubmitting) "Saving..." else if (isEditMode) "Save Changes" else "Add Account")
                }
            }
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
