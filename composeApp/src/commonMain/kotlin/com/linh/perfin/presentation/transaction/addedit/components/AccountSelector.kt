package com.linh.perfin.presentation.transaction.addedit.components

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
import com.linh.perfin.domain.model.account.Account

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSelector(
    selectedAccountId: String,
    accounts: List<Account>,
    onAccountSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String? = null,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

    val selectedAccount = accounts.find { it.id == selectedAccountId }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded && enabled },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedAccount?.name ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Account *") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            isError = isError,
            supportingText = if (isError && errorMessage != null) {
                { Text(errorMessage, color = MaterialTheme.colorScheme.error) }
            } else null,
            enabled = enabled
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            accounts.forEach { account ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = account.name,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    onClick = {
                        onAccountSelected(account.id)
                        expanded = false
                    }
                )
            }
        }
    }
}
