package com.linh.perfin.presentation.split

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.linh.perfin.domain.model.split.Participant
import com.linh.perfin.domain.model.split.SplitAllocation
import com.linh.perfin.domain.model.split.SplitMode
import com.linh.perfin.domain.model.split.SplitTransaction
import com.linh.perfin.domain.usecase.split.CalculateSplitAmountsUseCase
import com.linh.perfin.domain.usecase.split.CreateParticipantUseCase
import com.linh.perfin.domain.usecase.split.DeleteSplitTransactionUseCase
import com.linh.perfin.domain.usecase.split.GetAllParticipantsUseCase
import com.linh.perfin.domain.usecase.split.GetSplitByTransactionIdUseCase
import com.linh.perfin.domain.usecase.split.SaveSplitTransactionUseCase
import com.linh.perfin.domain.usecase.split.ValidateSplitTransactionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
class SplitTransactionViewModel(
    private val transactionId: String,
    private val transactionAmount: BigDecimal,
    private val getAllParticipantsUseCase: GetAllParticipantsUseCase,
    private val getSplitByTransactionIdUseCase: GetSplitByTransactionIdUseCase,
    private val saveSplitTransactionUseCase: SaveSplitTransactionUseCase,
    private val calculateSplitAmountsUseCase: CalculateSplitAmountsUseCase,
    private val validateSplitTransactionUseCase: ValidateSplitTransactionUseCase,
    private val deleteSplitTransactionUseCase: DeleteSplitTransactionUseCase,
    private val createParticipantUseCase: CreateParticipantUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<SplitTransactionUiState>(SplitTransactionUiState.Loading)
    val uiState: StateFlow<SplitTransactionUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            try {
                val participants = getAllParticipantsUseCase().first()
                val existingSplit = getSplitByTransactionIdUseCase(transactionId)

                val formState = if (existingSplit != null) {
                    // Edit mode - load existing split
                    SplitFormState(
                        splitTransactionId = existingSplit.id,
                        splitMode = existingSplit.splitMode,
                        allocations = existingSplit.allocations.map { allocation ->
                            AllocationFormItem(
                                participant = allocation.participant,
                                inputValue = allocation.allocationValue,
                                calculatedAmount = allocation.calculatedAmount
                            )
                        }
                    )
                } else {
                    // New split - start with empty state
                    SplitFormState()
                }

                _uiState.value = SplitTransactionUiState.Editing(
                    transactionId = transactionId,
                    transactionAmount = transactionAmount,
                    formState = formState,
                    participants = participants
                )
            } catch (e: Exception) {
                _uiState.value = SplitTransactionUiState.Error(
                    e.message ?: "Failed to load data"
                )
            }
        }
    }

    fun updateSplitMode(mode: SplitMode) {
        updateFormState { state ->
            val recalculated = recalculateAllocations(
                state.copy(splitMode = mode),
                transactionAmount
            )
            recalculated.copy(isDirty = true)
        }
    }

    fun addParticipant(participant: Participant) {
        updateFormState { state ->
            val newAllocation = AllocationFormItem(
                participant = participant,
                inputValue = BigDecimal.ZERO,
                calculatedAmount = BigDecimal.ZERO
            )
            val updated = state.copy(
                allocations = state.allocations + newAllocation,
                isDirty = true
            )
            recalculateAllocations(updated, transactionAmount)
        }
    }

    fun removeParticipant(allocationId: String) {
        updateFormState { state ->
            val updated = state.copy(
                allocations = state.allocations.filter { it.id != allocationId },
                isDirty = true
            )
            recalculateAllocations(updated, transactionAmount)
        }
    }

    fun updateAllocationValue(allocationId: String, value: String) {
        updateFormState { state ->
            val numericValue = try { BigDecimal.parseString(value) } catch (e: Exception) { BigDecimal.ZERO }
            val updatedAllocations = state.allocations.map { allocation ->
                if (allocation.id == allocationId) {
                    allocation.copy(inputValue = numericValue)
                } else {
                    allocation
                }
            }
            val updated = state.copy(
                allocations = updatedAllocations,
                isDirty = true
            )
            recalculateAllocations(updated, transactionAmount)
        }
    }

    private fun recalculateAllocations(
        state: SplitFormState,
        totalAmount: BigDecimal
    ): SplitFormState {
        if (state.allocations.isEmpty()) {
            return state.copy(validationError = null)
        }

        val absoluteAmount = if (totalAmount < BigDecimal.ZERO) -totalAmount else totalAmount

        // Convert form items to split allocations for calculation
        val tempAllocations = state.allocations.map { item ->
            SplitAllocation(
                id = item.id,
                splitTransactionId = state.splitTransactionId ?: "",
                participant = item.participant ?: Participant(
                    id = "",
                    name = "Unknown",
                    createdAt = Clock.System.now(),
                    updatedAt = Clock.System.now()
                ),
                allocationValue = item.inputValue,
                calculatedAmount = item.calculatedAmount,
                createdAt = Clock.System.now(),
                updatedAt = Clock.System.now()
            )
        }

        val calculatedAllocations = calculateSplitAmountsUseCase(
            totalAmount = absoluteAmount,
            splitMode = state.splitMode,
            allocations = tempAllocations
        )

        // Convert back to form items
        val updatedFormItems = calculatedAllocations.mapIndexed { index, allocation ->
            state.allocations[index].copy(
                inputValue = allocation.allocationValue,
                calculatedAmount = allocation.calculatedAmount
            )
        }

        // Validate
        val validationError = validateFormState(
            state.copy(allocations = updatedFormItems),
            absoluteAmount
        )

        return state.copy(
            allocations = updatedFormItems,
            validationError = validationError
        )
    }

    private fun validateFormState(state: SplitFormState, totalAmount: BigDecimal): String? {
        if (state.allocations.size < 2) {
            return "Add at least 2 participants"
        }

        if (state.allocations.any { it.participant == null }) {
            return "All participants must be selected"
        }

        return when (state.splitMode) {
            SplitMode.EQUAL -> null

            SplitMode.PERCENTAGE -> {
                val total = state.allocations.fold(BigDecimal.ZERO) { acc, item ->
                    acc + item.inputValue
                }
                if (total != BigDecimal.fromInt(100)) {
                    "Percentages must sum to 100% (currently: ${total.toPlainString()}%)"
                } else null
            }

            SplitMode.AMOUNT -> {
                val total = state.allocations.fold(BigDecimal.ZERO) { acc, item ->
                    acc + item.calculatedAmount
                }
                val difference = if (total > totalAmount) total - totalAmount else totalAmount - total
                val tolerance = BigDecimal.fromDouble(0.01)

                if (difference > tolerance) {
                    "Amounts must sum to ${totalAmount.toPlainString()} (currently: ${total.toPlainString()})"
                } else null
            }

            SplitMode.SHARES -> {
                if (state.allocations.any { it.inputValue <= BigDecimal.ZERO }) {
                    "All shares must be greater than 0"
                } else null
            }
        }
    }

    fun submitForm() {
        val currentState = (_uiState.value as? SplitTransactionUiState.Editing) ?: return
        val formState = currentState.formState

        if (!formState.isValid()) {
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = currentState.copy(isSubmitting = true)

                val splitTransaction = SplitTransaction(
                    id = formState.splitTransactionId ?: Uuid.random().toString(),
                    parentTransactionId = transactionId,
                    splitMode = formState.splitMode,
                    allocations = formState.allocations.map { item ->
                        SplitAllocation(
                            id = Uuid.random().toString(),
                            splitTransactionId = formState.splitTransactionId ?: "",
                            participant = item.participant!!,
                            allocationValue = item.inputValue,
                            calculatedAmount = item.calculatedAmount,
                            createdAt = Clock.System.now(),
                            updatedAt = Clock.System.now()
                        )
                    },
                    createdAt = Clock.System.now(),
                    updatedAt = Clock.System.now()
                )

                saveSplitTransactionUseCase(
                    transactionId,
                    transactionAmount,
                    splitTransaction
                )

                _uiState.value = SplitTransactionUiState.SubmitSuccess
            } catch (e: Exception) {
                _uiState.value = currentState.copy(
                    isSubmitting = false,
                    error = e.message ?: "Failed to save split"
                )
            }
        }
    }

    fun deleteSplit() {
        val currentState = (_uiState.value as? SplitTransactionUiState.Editing) ?: return

        viewModelScope.launch {
            try {
                _uiState.value = currentState.copy(isSubmitting = true)
                deleteSplitTransactionUseCase(transactionId)
                _uiState.value = SplitTransactionUiState.SubmitSuccess
            } catch (e: Exception) {
                _uiState.value = currentState.copy(
                    isSubmitting = false,
                    error = e.message ?: "Failed to delete split"
                )
            }
        }
    }

    suspend fun createAndAddParticipant(name: String, email: String? = null, phone: String? = null): Result<Participant> {
        return try {
            val now = Clock.System.now()
            val participant = Participant(
                name = name.trim(),
                email = email?.trim()?.takeIf { it.isNotBlank() },
                phone = phone?.trim()?.takeIf { it.isNotBlank() },
                createdAt = now,
                updatedAt = now
            )
            createParticipantUseCase(participant)

            // Reload participants list
            val currentState = (_uiState.value as? SplitTransactionUiState.Editing)
            if (currentState != null) {
                val updatedParticipants = getAllParticipantsUseCase().first()
                _uiState.value = currentState.copy(participants = updatedParticipants)
            }

            // Add the participant to the split
            addParticipant(participant)

            Result.success(participant)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun canNavigateAway(): Boolean {
        val currentState = (_uiState.value as? SplitTransactionUiState.Editing) ?: return true
        return !currentState.formState.isDirty
    }

    fun clearError() {
        val currentState = (_uiState.value as? SplitTransactionUiState.Editing) ?: return
        _uiState.value = currentState.copy(error = null)
    }

    private fun updateFormState(update: (SplitFormState) -> SplitFormState) {
        val currentState = (_uiState.value as? SplitTransactionUiState.Editing) ?: return
        _uiState.value = currentState.copy(formState = update(currentState.formState))
    }
}
