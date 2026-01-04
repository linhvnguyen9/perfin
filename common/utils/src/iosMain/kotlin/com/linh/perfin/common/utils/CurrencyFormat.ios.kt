package com.linh.perfin.common.utils

import platform.Foundation.NSLocale
import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterCurrencyStyle
import platform.Foundation.currentLocale
import platform.Foundation.localeIdentifierFromComponents

actual fun formatCurrency(amount: Double, currencyCode: String, useSymbol: Boolean): String {
    val formatter = NSNumberFormatter().apply {
        numberStyle = NSNumberFormatterCurrencyStyle
        setCurrencyCode(currencyCode)

        when (currencyCode) {
            "VND", "JPY", "KRW" -> {
                maximumFractionDigits = 0u
                minimumFractionDigits = 0u
            }
            else -> {
            }
        }

        if (!useSymbol) {
            currencySymbol = currencyCode
        }

        locale = getLocaleForCurrency(currencyCode)
    }

    return formatter.stringFromNumber(NSNumber(amount)) ?: throw IllegalArgumentException("Invalid amount")
}

private fun getLocaleForCurrency(currencyCode: String): NSLocale {
    val localeId = when (currencyCode) {
        "USD" -> "en_US"
        "EUR" -> "de_DE"  // or any Eurozone country
        "GBP" -> "en_GB"
        "JPY" -> "ja_JP"
        "VND" -> "vi_VN"
        "CNY" -> "zh_CN"
        "RUB" -> "ru_RU"
        "INR" -> "hi_IN"
        else -> {
            // Try to get a locale that uses this currency by default
            val components: Map<Any?, *> = mapOf("cu" to currencyCode)
            NSLocale.localeIdentifierFromComponents(components) ?: "en_US"
        }
    }

    return NSLocale(localeId) ?: NSLocale.currentLocale
}