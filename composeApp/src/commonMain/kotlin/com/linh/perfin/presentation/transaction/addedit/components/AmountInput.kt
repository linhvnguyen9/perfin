package com.linh.perfin.presentation.transaction.addedit.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.linh.perfin.common.utils.formatCurrency

@Composable
fun AmountInput(
    amount: String,
    onAmountChange: (String) -> Unit,
    onBlur: () -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String? = null,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = amount,
        onValueChange = onAmountChange,
        label = { Text("Amount *") },
        placeholder = { Text("0") },
        prefix = { Text("₫ ", style = MaterialTheme.typography.bodyLarge) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        isError = isError,
        supportingText = if (isError && errorMessage != null) {
            { Text(errorMessage, color = MaterialTheme.colorScheme.error) }
        } else {
            val amountValue = amount.toDoubleOrNull()
            if (amountValue != null && amountValue > 0) {
                { Text(formatCurrency(amountValue, "VND", useSymbol = true)) }
            } else null
        },
        modifier = modifier.fillMaxWidth(),
        enabled = enabled
    )
}
