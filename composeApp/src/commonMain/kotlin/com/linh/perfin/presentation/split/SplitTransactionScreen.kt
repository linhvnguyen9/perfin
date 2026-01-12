package com.linh.perfin.presentation.split

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.linh.perfin.domain.model.split.Participant
import com.linh.perfin.domain.model.split.SplitMode
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import perfin.composeapp.generated.resources.Res
import perfin.composeapp.generated.resources.arrow_back_24px
import perfin.composeapp.generated.resources.delete_24px
import perfin.composeapp.generated.resources.qr_code_24px

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplitTransactionScreen(
    transactionId: String,
    transactionAmount: String,
    onNavigateBack: () -> Unit,
    onNavigateToQr: (String, String, String) -> Unit = { _, _, _ -> },
    modifier: Modifier = Modifier,
    viewModel: SplitTransactionViewModel = koinViewModel(
        parameters = {
            parametersOf(transactionId, transactionAmount)
        }
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showAddParticipantDialog by remember { mutableStateOf(false) }
    var showCreateParticipantMode by remember { mutableStateOf(false) }
    var newParticipantName by remember { mutableStateOf("") }
    var newParticipantEmail by remember { mutableStateOf("") }
    var newParticipantPhone by remember { mutableStateOf("") }
    var isCreatingParticipant by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Handle success navigation
    LaunchedEffect(uiState) {
        if (uiState is SplitTransactionUiState.SubmitSuccess) {
            onNavigateBack()
        }
    }

    // Handle error snackbar
    LaunchedEffect(uiState) {
        val currentState = uiState
        if (currentState is SplitTransactionUiState.Editing && currentState.error != null) {
            snackbarHostState.showSnackbar(currentState.error)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Split Transaction") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(Res.drawable.arrow_back_24px),
                            contentDescription = "Navigate back"
                        )
                    }
                },
                actions = {
                    val editingState = uiState as? SplitTransactionUiState.Editing
                    if (editingState?.formState?.splitTransactionId != null) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                painter = painterResource(Res.drawable.delete_24px),
                                contentDescription = "Delete split",
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
            is SplitTransactionUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is SplitTransactionUiState.Editing -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Amount Display
                    item {
                        Card {
                            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                                Text("Transaction Amount", style = MaterialTheme.typography.labelMedium)
                                Text(state.transactionAmount.toPlainString(), style = MaterialTheme.typography.headlineMedium)
                            }
                        }
                    }

                    // Split Mode Selector
                    item {
                        Text("Split Method", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                    }

                    item {
                        SplitMode.entries.forEach { mode ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(mode.displayName, modifier = Modifier.weight(1f))
                                RadioButton(
                                    selected = state.formState.splitMode == mode,
                                    onClick = { viewModel.updateSplitMode(mode) },
                                    enabled = !state.isSubmitting
                                )
                            }
                        }
                    }

                    // Participants
                    item {
                        Text("Participants", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                    }

                    items(
                        items = state.formState.allocations,
                        key = { it.id }
                    ) { allocation ->
                        val allocationId = allocation.id

                        // Read the current allocation from state to ensure we always have the latest data
                        val currentAllocation = remember(state.formState.allocations, allocationId) {
                            state.formState.allocations.find { it.id == allocationId }
                        } ?: allocation

                        Card {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(currentAllocation.participant?.name ?: "Unknown", style = MaterialTheme.typography.bodyLarge)
                                    Text("Amount: ${currentAllocation.calculatedAmount.toPlainString()}", style = MaterialTheme.typography.bodySmall)
                                }

                                if (state.formState.splitMode != SplitMode.EQUAL) {
                                    OutlinedTextField(
                                        value = currentAllocation.inputValue.toPlainString(),
                                        onValueChange = { viewModel.updateAllocationValue(allocationId, it) },
                                        modifier = Modifier.width(100.dp),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                        singleLine = true,
                                        enabled = !state.isSubmitting
                                    )
                                }

                                // QR Code Button
                                IconButton(
                                    onClick = {
                                        currentAllocation.participant?.let { participant ->
                                            onNavigateToQr(
                                                transactionId,
                                                participant.id,
                                                currentAllocation.calculatedAmount.toPlainString()
                                            )
                                        }
                                    },
                                    enabled = !state.isSubmitting && currentAllocation.participant != null
                                ) {
                                    Icon(
                                        painter = painterResource(Res.drawable.qr_code_24px),
                                        contentDescription = "Show QR code",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }

                                IconButton(onClick = { viewModel.removeParticipant(allocationId) }, enabled = !state.isSubmitting) {
                                    Icon(painterResource(Res.drawable.delete_24px), "Remove", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }

                    // Add Participant Button
                    item {
                        OutlinedButton(
                            onClick = { showAddParticipantDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !state.isSubmitting
                        ) {
                            Text("Add Participant")
                        }
                    }

                    // Validation Summary
                    if (state.formState.validationError != null) {
                        item {
                            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                                Text(
                                    state.formState.validationError,
                                    modifier = Modifier.padding(16.dp),
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }

                    // Action Buttons
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            OutlinedButton(
                                onClick = onNavigateBack,
                                modifier = Modifier.weight(1f),
                                enabled = !state.isSubmitting
                            ) {
                                Text("Cancel")
                            }
                            Button(
                                onClick = { viewModel.submitForm() },
                                modifier = Modifier.weight(1f),
                                enabled = state.formState.isValid() && !state.isSubmitting
                            ) {
                                Text(if (state.isSubmitting) "Saving..." else "Save")
                            }
                        }
                    }
                }
            }

            is SplitTransactionUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    Text("Error: ${state.message}")
                }
            }

            is SplitTransactionUiState.SubmitSuccess -> {
                // Handled by LaunchedEffect
            }
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Split") },
            text = { Text("Are you sure you want to delete this split?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteSplit()
                    showDeleteDialog = false
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Add Participant Dialog
    if (showAddParticipantDialog) {
        val editingState = uiState as? SplitTransactionUiState.Editing
        if (editingState != null) {
            // Filter out participants that are already added
            val addedParticipantIds = editingState.formState.allocations
                .mapNotNull { it.participant?.id }
                .toSet()
            val availableParticipants = editingState.participants
                .filter { it.id !in addedParticipantIds }

            AlertDialog(
                onDismissRequest = {
                    showAddParticipantDialog = false
                    showCreateParticipantMode = false
                    newParticipantName = ""
                    newParticipantEmail = ""
                    newParticipantPhone = ""
                },
                title = { Text(if (showCreateParticipantMode) "Create New Participant" else "Add Participant") },
                text = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        if (!showCreateParticipantMode) {
                            // List mode - show existing participants
                            if (availableParticipants.isNotEmpty()) {
                                LazyColumn(modifier = Modifier.weight(1f, fill = false)) {
                                    items(availableParticipants) { participant ->
                                        TextButton(
                                            onClick = {
                                                viewModel.addParticipant(participant)
                                                showAddParticipantDialog = false
                                            },
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = participant.name,
                                                modifier = Modifier.fillMaxWidth(),
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                        }
                                    }
                                }
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                            }

                            TextButton(
                                onClick = { showCreateParticipantMode = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("+ Create New Participant")
                            }
                        } else {
                            // Create mode - show form
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                OutlinedTextField(
                                    value = newParticipantName,
                                    onValueChange = { newParticipantName = it },
                                    label = { Text("Name *") },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = !isCreatingParticipant,
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = newParticipantEmail,
                                    onValueChange = { newParticipantEmail = it },
                                    label = { Text("Email (optional)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = !isCreatingParticipant,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = newParticipantPhone,
                                    onValueChange = { newParticipantPhone = it },
                                    label = { Text("Phone (optional)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = !isCreatingParticipant,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    singleLine = true
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    if (showCreateParticipantMode) {
                        Button(
                            onClick = {
                                isCreatingParticipant = true
                                coroutineScope.launch {
                                    val result = viewModel.createAndAddParticipant(
                                        name = newParticipantName,
                                        email = newParticipantEmail.takeIf { it.isNotBlank() },
                                        phone = newParticipantPhone.takeIf { it.isNotBlank() }
                                    )
                                    isCreatingParticipant = false
                                    if (result.isSuccess) {
                                        showAddParticipantDialog = false
                                        showCreateParticipantMode = false
                                        newParticipantName = ""
                                        newParticipantEmail = ""
                                        newParticipantPhone = ""
                                    } else {
                                        snackbarHostState.showSnackbar(
                                            result.exceptionOrNull()?.message ?: "Failed to create participant"
                                        )
                                    }
                                }
                            },
                            enabled = newParticipantName.trim().isNotBlank() && !isCreatingParticipant
                        ) {
                            Text(if (isCreatingParticipant) "Creating..." else "Create & Add")
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        if (showCreateParticipantMode) {
                            showCreateParticipantMode = false
                            newParticipantName = ""
                            newParticipantEmail = ""
                            newParticipantPhone = ""
                        } else {
                            showAddParticipantDialog = false
                        }
                    }) {
                        Text(if (showCreateParticipantMode) "Back" else "Cancel")
                    }
                }
            )
        }
    }
}
