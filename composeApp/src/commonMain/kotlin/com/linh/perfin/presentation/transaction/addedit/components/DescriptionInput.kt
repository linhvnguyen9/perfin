package com.linh.perfin.presentation.transaction.addedit.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction

@Composable
fun DescriptionInput(
    description: String,
    onDescriptionChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String? = null,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = description,
        onValueChange = onDescriptionChange,
        label = { Text("Description *") },
        placeholder = { Text("What was this transaction for?") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        isError = isError,
        supportingText = if (isError && errorMessage != null) {
            { Text(errorMessage, color = MaterialTheme.colorScheme.error) }
        } else null,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled
    )
}
