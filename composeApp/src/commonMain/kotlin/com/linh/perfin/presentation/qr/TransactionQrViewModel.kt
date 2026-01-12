package com.linh.perfin.presentation.qr

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.linh.perfin.domain.usecase.GenerateVietQrCodeUseCase
import com.linh.perfin.domain.usecase.account.GetAccountByIdUseCase
import com.linh.perfin.domain.usecase.split.GetParticipantByIdUseCase
import com.linh.perfin.domain.usecase.transaction.GetTransactionByIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class TransactionQrViewModel(
    private val transactionId: String,
    private val participantId: String,
    private val splitAmount: BigDecimal,
    private val getTransactionByIdUseCase: GetTransactionByIdUseCase,
    private val getAccountByIdUseCase: GetAccountByIdUseCase,
    private val getParticipantByIdUseCase: GetParticipantByIdUseCase,
    private val generateVietQrCodeUseCase: GenerateVietQrCodeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<TransactionQrUiState>(TransactionQrUiState.Loading)
    val uiState: StateFlow<TransactionQrUiState> = _uiState.asStateFlow()

    init {
        loadQrData()
    }

    private fun loadQrData() {
        viewModelScope.launch {
            try {
                // Fetch transaction
                val transaction = getTransactionByIdUseCase(transactionId)
                    ?: throw Exception("Transaction not found")

                // Fetch account
                val account = getAccountByIdUseCase(transaction.accountId)
                    ?: throw Exception("Account not found")

                // Validate account has required data
                if (account.accountNumber.isBlank()) {
                    throw Exception("Account number is missing. Please update account details.")
                }
                if (account.bank == null) {
                    throw Exception("Bank information is missing. Please update account details.")
                }

                // Fetch participant
                val participant = getParticipantByIdUseCase(participantId)
                    ?: throw Exception("Participant not found")

                // Generate QR message
                val message = "Payment from ${participant.name} - ${transaction.description} - Split ${transactionId.take(8)}"

                // Generate QR code data
                val qrCodeData = generateVietQrCodeUseCase(
                    recipientAccount = account.accountNumber,
                    bank = account.bank,
                    amount = splitAmount.toPlainString(),
                    transactionMessage = message
                )

                _uiState.value = TransactionQrUiState.Success(
                    qrCodeData = qrCodeData,
                    amount = splitAmount.toPlainString(),
                    participantName = participant.name,
                    transactionDescription = transaction.description,
                    recipientAccountNumber = account.accountNumber,
                    recipientBankName = account.bank.name
                )

            } catch (e: Exception) {
                _uiState.value = TransactionQrUiState.Error(
                    e.message ?: "Failed to generate QR code"
                )
            }
        }
    }
}

sealed class TransactionQrUiState {
    data object Loading : TransactionQrUiState()

    data class Success(
        val qrCodeData: String,
        val amount: String,
        val participantName: String,
        val transactionDescription: String,
        val recipientAccountNumber: String,
        val recipientBankName: String
    ) : TransactionQrUiState()

    data class Error(val message: String) : TransactionQrUiState()
}
