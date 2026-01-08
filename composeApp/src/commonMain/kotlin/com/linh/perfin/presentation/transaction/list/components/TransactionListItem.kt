package com.linh.perfin.presentation.transaction.list.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.linh.perfin.common.utils.FormatStyle
import com.linh.perfin.common.utils.formatCurrency
import com.linh.perfin.common.utils.formatDate
import com.linh.perfin.domain.model.transaction.TransactionWithDetails
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun TransactionListItem(
    transaction: TransactionWithDetails,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category icon in colored circle
            Surface(
                shape = CircleShape,
                color = transaction.categoryColor?.let { parseColor(it) }
                    ?: MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(40.dp)
            ) {
                Box(
                    modifier = Modifier.size(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = transaction.categoryIcon ?: "💰",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            // Transaction details
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = transaction.transaction.description,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = transaction.accountName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = transaction.transaction.date
                        .toLocalDateTime(TimeZone.currentSystemDefault())
                        .formatDate(style = FormatStyle.MEDIUM),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Amount with color coding
            Text(
                text = formatCurrency(
                    transaction.transaction.amount.doubleValue(),
                    "VND",
                    useSymbol = true
                ),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = getAmountColor(transaction.transaction.amount.doubleValue())
            )
        }
    }
}

private fun parseColor(colorString: String): Color {
    return try {
        // Handle hex color strings (e.g., "#FF0000" or "FF0000")
        val hex = colorString.removePrefix("#")
        when (hex.length) {
            6 -> {
                val colorInt = hex.toLong(16)
                Color(0xFF000000 or colorInt)
            }
            8 -> {
                val colorInt = hex.toLong(16)
                Color(colorInt)
            }
            else -> Color.Gray
        }
    } catch (e: Exception) {
        Color.Gray
    }
}

private fun getAmountColor(amount: Double): Color {
    return when {
        amount > 0 -> Color(0xFF4CAF50) // Green for income
        amount < 0 -> Color(0xFFF44336) // Red for expense
        else -> Color.Gray
    }
}
