package com.linh.perfin.common.utils

import kotlin.time.Instant

fun Long?.toBoolean() = this == 1L

fun Long?.toInstant() = this?.let { Instant.fromEpochMilliseconds(this) }

fun Long.toInstant() = Instant.fromEpochMilliseconds(this)