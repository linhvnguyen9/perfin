package com.linh.perfin.presentation.transaction.addedit.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FormActionButtons(
    onSave: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    isEditMode: Boolean = false,
    isSaving: Boolean = false,
    canSave: Boolean = true
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier.weight(1f),
            enabled = !isSaving
        ) {
            Text("Cancel")
        }

        Button(
            onClick = onSave,
            modifier = Modifier.weight(1f),
            enabled = canSave && !isSaving
        ) {
            Text(if (isSaving) "Saving..." else if (isEditMode) "Save Changes" else "Add Transaction")
        }
    }
}
