package com.linh.perfin.presentation.account.addedit.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization

@Composable
fun AccountNameInput(
    name: String,
    onNameChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String? = null,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = name,
        onValueChange = onNameChange,
        label = { Text("Account Name *") },
        placeholder = { Text("e.g., My Checking Account") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next,
            capitalization = KeyboardCapitalization.Words
        ),
        isError = isError,
        supportingText = if (isError && errorMessage != null) {
            { Text(errorMessage, color = MaterialTheme.colorScheme.error) }
        } else null,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled
    )
}
