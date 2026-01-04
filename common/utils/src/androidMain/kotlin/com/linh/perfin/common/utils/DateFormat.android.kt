package com.linh.perfin.common.utils

import android.icu.text.DateFormat
import android.icu.text.DateFormatSymbols
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toJavaLocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import kotlin.time.toJavaInstant

// Can use kotlinx.datetime built in format and remove this function
// once this is complete https://github.com/Kotlin/kotlinx-datetime/discussions/253
actual fun LocalDateTime.format(timeZone: TimeZone, format: String): String {
    val formatter = DateTimeFormatter.ofPattern(format)
    return formatter.format(this.toJavaLocalDateTime())
}

actual fun LocalDateTime.formatDate(timeZone: TimeZone, style: FormatStyle): String {
    val dateFormat = DateFormat.getDateInstance(style.toDateFormatFormatStyle(), Locale.getDefault())
    val instant = this.toInstant(timeZone).toJavaInstant()
    val date: Date = Date.from(instant)
    return dateFormat.format(date)
}

actual fun LocalDateTime.formatTime(timeZone: TimeZone, style: FormatStyle): String {
    val dateFormat = java.text.DateFormat.getTimeInstance(style.toDateFormatFormatStyle(), Locale.getDefault())
    val instant = this.toInstant(timeZone).toJavaInstant()
    val date: Date = Date.from(instant)
    return dateFormat.format(date)
}

actual fun LocalDateTime.formatDateTime(timeZone: TimeZone, dateStyle: FormatStyle, timeStyle: FormatStyle): String {
    val dateFormat = java.text.DateFormat.getDateTimeInstance(dateStyle.toDateFormatFormatStyle(), timeStyle.toDateFormatFormatStyle(), Locale.getDefault())
    val instant = this.toInstant(timeZone).toJavaInstant()
    val date: Date = Date.from(instant)
    return dateFormat.format(date)
}

private fun FormatStyle.toDateFormatFormatStyle(): Int {
    return when(this) {
        FormatStyle.SHORT -> DateFormat.SHORT
        FormatStyle.MEDIUM -> DateFormat.MEDIUM
        FormatStyle.LONG -> DateFormat.LONG
        FormatStyle.FULL -> DateFormat.FULL
    }
}

actual fun Int.getShortMonthName(): String {
    val months = DateFormatSymbols(Locale.getDefault()).months
    return months[this - 1]
}