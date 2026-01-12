package com.linh.perfin.presentation.account.addedit.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun AccountNumberInput(
    accountNumber: String,
    onAccountNumberChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String? = null,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = accountNumber,
        onValueChange = onAccountNumberChange,
        label = { Text("Account Number") },
        placeholder = { Text("e.g., 1234567890") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.Number
        ),
        isError = isError,
        supportingText = if (isError && errorMessage != null) {
            { Text(errorMessage, color = MaterialTheme.colorScheme.error) }
        } else null,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled
    )
}
