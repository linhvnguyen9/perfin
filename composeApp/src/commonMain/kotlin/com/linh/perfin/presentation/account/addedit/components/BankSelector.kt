package com.linh.perfin.presentation.account.addedit.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.linh.perfin.domain.model.BankInVietnam

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BankSelector(
    selectedBank: BankInVietnam?,
    onBankSelected: (BankInVietnam?) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

    // Format bank name for display (convert enum name to readable format)
    fun formatBankName(bank: BankInVietnam): String {
        return bank.name.replace("_", " ").lowercase()
            .split(" ")
            .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded && enabled },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedBank?.let { formatBankName(it) } ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Bank (Optional)") },
            placeholder = { Text("Select a bank") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            enabled = enabled
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            // Option to clear selection
            if (selectedBank != null) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "None",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    onClick = {
                        onBankSelected(null)
                        expanded = false
                    }
                )
            }

            // All 57 banks from BankInVietnam enum (sorted alphabetically)
            BankInVietnam.entries
                .sortedBy { formatBankName(it) }
                .forEach { bank ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = formatBankName(bank),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        },
                        onClick = {
                            onBankSelected(bank)
                            expanded = false
                        }
                    )
                }
        }
    }
}
