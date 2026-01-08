package com.linh.perfin.presentation.transaction.addedit.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.linh.perfin.domain.model.transaction.TransactionType

@Composable
fun TransactionTypeToggle(
    selectedType: TransactionType,
    onTypeSelected: (TransactionType) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    SingleChoiceSegmentedButtonRow(
        modifier = modifier.fillMaxWidth()
    ) {
        SegmentedButton(
            selected = selectedType == TransactionType.INCOME,
            onClick = { onTypeSelected(TransactionType.INCOME) },
            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
            enabled = enabled
        ) {
            Text(
                text = "Income",
                style = MaterialTheme.typography.labelLarge
            )
        }

        SegmentedButton(
            selected = selectedType == TransactionType.EXPENSE,
            onClick = { onTypeSelected(TransactionType.EXPENSE) },
            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
            enabled = enabled
        ) {
            Text(
                text = "Expense",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}
