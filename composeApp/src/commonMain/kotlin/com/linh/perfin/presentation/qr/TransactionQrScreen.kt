package com.linh.perfin.presentation.qr

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.alexzhirkevich.qrose.options.*
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import perfin.composeapp.generated.resources.Res
import perfin.composeapp.generated.resources.arrow_back_24px

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionQrScreen(
    transactionId: String,
    participantId: String,
    splitAmount: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TransactionQrViewModel = koinViewModel(
        parameters = {
            parametersOf(transactionId, participantId, splitAmount)
        }
    )
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Payment QR Code") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(Res.drawable.arrow_back_24px),
                            contentDescription = "Navigate back"
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        when (val state = uiState) {
            is TransactionQrUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is TransactionQrUiState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Transaction Info Card
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Payment Amount",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(
                                state.amount,
                                style = MaterialTheme.typography.headlineLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "From: ${state.participantName}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                "For: ${state.transactionDescription}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    // QR Code Display
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize().padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            val qrColor = MaterialTheme.colorScheme.onSurface
                            val qrCodePainter = rememberQrCodePainter(state.qrCodeData) {
                                shapes {
                                    ball = QrBallShape.roundCorners(.25f)
                                    darkPixel = QrPixelShape.roundCorners()
                                    frame = QrFrameShape.roundCorners(.25f)
                                }
                                colors {
                                    dark = QrBrush.solid(qrColor)
                                    frame = QrBrush.solid(qrColor)
                                }
                            }

                            androidx.compose.foundation.Image(
                                painter = qrCodePainter,
                                contentDescription = "Payment QR Code",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    // Recipient Info Card
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Recipient Account",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                state.recipientAccountNumber,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                state.recipientBankName,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    // Instructions
                    Text(
                        "Scan this QR code with your banking app to send payment",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            is TransactionQrUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "Unable to generate QR code",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            state.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(onClick = onNavigateBack) {
                            Text("Go Back")
                        }
                    }
                }
            }
        }
    }
}
