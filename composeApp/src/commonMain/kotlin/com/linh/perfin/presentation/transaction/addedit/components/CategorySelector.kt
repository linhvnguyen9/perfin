package com.linh.perfin.presentation.transaction.addedit.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.linh.perfin.domain.model.category.Category

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySelector(
    selectedCategoryId: String?,
    categories: List<Category>,
    onCategorySelected: (String?) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

    val selectedCategory = categories.find { it.id == selectedCategoryId }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded && enabled },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedCategory?.name ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Category (Optional)") },
            placeholder = { Text("Select a category") },
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
            if (selectedCategoryId != null) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "None",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    onClick = {
                        onCategorySelected(null)
                        expanded = false
                    }
                )
            }

            categories.forEach { category ->
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (category.icon != null) {
                                Text(
                                    text = category.icon,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            Text(
                                text = category.name,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    },
                    onClick = {
                        onCategorySelected(category.id)
                        expanded = false
                    }
                )
            }
        }
    }
}
