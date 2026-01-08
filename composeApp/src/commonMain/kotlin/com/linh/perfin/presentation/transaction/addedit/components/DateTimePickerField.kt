package com.linh.perfin.presentation.transaction.addedit.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.linh.perfin.common.utils.FormatStyle
import com.linh.perfin.common.utils.formatDateTime
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import perfin.composeapp.generated.resources.Res
import perfin.composeapp.generated.resources.calendar_today_24px
import kotlin.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePickerField(
    selectedDateTime: Instant,
    onDateTimeSelected: (Instant) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    var showDatePicker by remember { mutableStateOf(false) }

    val localDateTime = selectedDateTime.toLocalDateTime(TimeZone.currentSystemDefault())
    val displayText = localDateTime.formatDateTime(
        dateStyle = FormatStyle.MEDIUM,
        timeStyle = FormatStyle.SHORT
    )

    OutlinedTextField(
        value = displayText,
        onValueChange = {},
        label = { Text("Date & Time *") },
        readOnly = true,
        trailingIcon = {
            Icon(
                painter = painterResource(Res.drawable.calendar_today_24px),
                contentDescription = "Select date and time"
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { showDatePicker = true },
        enabled = false, // Always disabled for text input, use click instead
        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            disabledBorderColor = MaterialTheme.colorScheme.outline,
            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDateTime.toEpochMilliseconds()
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            // For now, keep the existing time and only update the date
                            val newDate = Instant.fromEpochMilliseconds(millis)
                                .toLocalDateTime(TimeZone.currentSystemDefault())

                            val updatedDateTime = LocalDateTime(
                                year = newDate.year,
                                monthNumber = newDate.monthNumber,
                                dayOfMonth = newDate.dayOfMonth,
                                hour = localDateTime.hour,
                                minute = localDateTime.minute,
                                second = localDateTime.second,
                                nanosecond = localDateTime.nanosecond
                            )

                            onDateTimeSelected(
                                updatedDateTime.toInstant(TimeZone.currentSystemDefault())
                            )
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
