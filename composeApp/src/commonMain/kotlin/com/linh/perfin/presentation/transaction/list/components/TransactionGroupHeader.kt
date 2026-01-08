package com.linh.perfin.presentation.transaction.list.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.linh.perfin.common.utils.localDateNow
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus

@Composable
fun TransactionGroupHeader(
    date: LocalDate,
    modifier: Modifier = Modifier
) {
    val dateText = formatGroupDate(date)

    Text(
        text = dateText,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    )
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        color = MaterialTheme.colorScheme.outlineVariant
    )
}

private fun formatGroupDate(date: LocalDate): String {
    val today = localDateNow()
    val yesterday = today.minus(1, DateTimeUnit.DAY)

    return when (date) {
        today -> "Today"
        yesterday -> "Yesterday"
        else -> {
            // Format as "Jan 4, 2026"
            val monthName = date.monthNumber.getMonthAbbreviation()
            "$monthName ${date.dayOfMonth}, ${date.year}"
        }
    }
}

private fun Int.getMonthAbbreviation(): String {
    return when (this) {
        1 -> "Jan"
        2 -> "Feb"
        3 -> "Mar"
        4 -> "Apr"
        5 -> "May"
        6 -> "Jun"
        7 -> "Jul"
        8 -> "Aug"
        9 -> "Sep"
        10 -> "Oct"
        11 -> "Nov"
        12 -> "Dec"
        else -> ""
    }
}
