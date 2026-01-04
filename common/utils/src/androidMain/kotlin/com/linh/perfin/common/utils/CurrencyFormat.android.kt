package com.linh.perfin.common.utils

import android.icu.text.NumberFormat
import android.icu.util.Currency
import java.util.Locale

actual fun formatCurrency(amount: Double, currencyCode: String, useSymbol: Boolean): String {
    try {
        val currency = Currency.getInstance(currencyCode)
        val numberFormat = NumberFormat.getCurrencyInstance(getLocaleForCurrency(currencyCode))
        numberFormat.currency = currency

        // Special handling for currencies with no decimal places like VND
        if (currency.defaultFractionDigits == 0) {
            numberFormat.maximumFractionDigits = 0
            numberFormat.minimumFractionDigits = 0
        }

        // If we don't want to use the symbol, replace it with the code
        if (!useSymbol) {
            return numberFormat.format(amount).replace(currency.symbol, currency.currencyCode)
        }

        return numberFormat.format(amount)
    } catch (e: Exception) {
        // Fallback formatting if the currency code is invalid
        return "$currencyCode ${String.format(Locale.getDefault(), "%.2f", amount)}"
    }
}

/**
 * Gets the appropriate locale for a given currency code to ensure
 * proper formatting conventions are followed.
 */
private fun getLocaleForCurrency(currencyCode: String): Locale {
    return when (currencyCode) {
        "USD" -> Locale.US
        "EUR" -> Locale.GERMANY  // or any Eurozone country
        "GBP" -> Locale.UK
        "JPY" -> Locale.JAPAN
        "VND" -> Locale("vi", "VN")  // Vietnamese locale
        "CNY" -> Locale.CHINA
        "RUB" -> Locale("ru", "RU")
        "INR" -> Locale("hi", "IN")
        // Add more specific mappings as needed
        else -> {
            try {
                // Try to determine locale from currency
                val availableLocales = Locale.getAvailableLocales()
                val localeForCurrency = availableLocales.find { locale ->
                    try {
                        Currency.getInstance(locale) == Currency.getInstance(currencyCode)
                    } catch (e: Exception) {
                        false
                    }
                }
                localeForCurrency ?: Locale.US  // Default to US if no matching locale
            } catch (e: Exception) {
                Locale.US  // Default to US locale
            }
        }
    }
}