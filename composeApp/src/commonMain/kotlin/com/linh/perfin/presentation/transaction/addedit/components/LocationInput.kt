package com.linh.perfin.presentation.transaction.addedit.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun LocationInput(
    location: String,
    onLocationChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String? = null,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = location,
        onValueChange = onLocationChange,
        label = { Text("Location (Optional)") },
        placeholder = { Text("Where did this transaction occur?") },
        singleLine = true,
        isError = isError,
        supportingText = if (isError && errorMessage != null) {
            { Text(errorMessage, color = MaterialTheme.colorScheme.error) }
        } else null,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled
    )
}
