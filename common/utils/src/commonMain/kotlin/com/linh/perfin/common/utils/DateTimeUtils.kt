package com.linh.perfin.common.utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

fun localDateNow(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDate {
    return Clock.System.now().toLocalDateTime(timeZone).date
}

fun localTimeNow(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalTime {
    return Clock.System.now().toLocalDateTime(timeZone).time
}