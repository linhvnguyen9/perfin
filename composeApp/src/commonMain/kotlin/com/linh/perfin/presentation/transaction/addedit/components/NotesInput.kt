package com.linh.perfin.presentation.transaction.addedit.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun NotesInput(
    notes: String,
    onNotesChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String? = null,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = notes,
        onValueChange = onNotesChange,
        label = { Text("Notes (Optional)") },
        placeholder = { Text("Add any additional details...") },
        maxLines = 4,
        minLines = 3,
        isError = isError,
        supportingText = if (isError && errorMessage != null) {
            { Text(errorMessage, color = MaterialTheme.colorScheme.error) }
        } else {
            { Text("${notes.length}/1000", style = MaterialTheme.typography.bodySmall) }
        },
        modifier = modifier.fillMaxWidth(),
        enabled = enabled
    )
}
