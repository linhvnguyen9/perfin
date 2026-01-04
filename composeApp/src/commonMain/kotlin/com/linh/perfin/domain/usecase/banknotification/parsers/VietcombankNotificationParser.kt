package com.linh.perfin.domain.usecase.banknotification.parsers

import com.linh.perfin.domain.model.transaction.Transaction
import com.linh.perfin.domain.model.transaction.TransactionType
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.linh.perfin.common.utils.removeVietnameseAccent
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class VietcombankNotificationParser: NotificationParser {
    override fun isFromApp(packageName: String): Boolean = packageName == "com.VCB"

    override fun parseNotification(title: String, text: String): Transaction? {
        val parsedText = text.removeVietnameseAccent()
        println(parsedText)
        val match = regex.find(parsedText)
        val matchGroups = (match?.groups as? MatchNamedGroupCollection)
        matchGroups?.let {
            val accountNumber = it[REGEX_KEY_ACCOUNT_NUMBER]?.value.orEmpty()
            val balanceChange = it[REGEX_KEY_BALANCE_CHANGE]?.value.orEmpty()
            val currency =  it[REGEX_KEY_CURRENCY]?.value.orEmpty()
            val balanceCurrency =  it[REGEX_KEY_BALANCE_CURRENCY]?.value.orEmpty()
            val transactionDetails =  it[REGEX_KEY_TRANSACTION_NAME]?.value.orEmpty()
                .split(".")

            val transactionType = if (balanceChange.startsWith("-")) TransactionType.EXPENSE else TransactionType.INCOME
            val transactionValue = balanceChange
                .substring(1 until balanceChange.length)
                .replace(",", "")

            val transactionName = if (transactionType == TransactionType.INCOME) {
                transactionDetails
                    .last()
                    .replace(" FT\\d+".toRegex(), "")
            } else {
                println(transactionDetails[0])
                transactionDetails[transactionDetails.lastIndex - 1]
            }

            println("transaction value $transactionValue")

            return Transaction(
                accountId = "", // Will be set by ParseNotificationUseCase
                amount = BigDecimal.parseString(transactionValue),
                description = transactionName,
                date = Clock.System.now(),
                notes = text,
                location = "",
                createdAt = Clock.System.now(),
                updatedAt = Clock.System.now(),
                isReconciled = false,
            )
        }

        return null
    }

    val regex = "So du TK VCB (?<$REGEX_KEY_ACCOUNT_NUMBER>\\d+) (?<$REGEX_KEY_BALANCE_CHANGE>[+-]((\\d{1,3}(,\\d{3})*)|(\\d*))(\\.|\\.\\d*)?) (?<$REGEX_KEY_CURRENCY>\\w+) luoc \\d{2}-\\d{2}-\\d{4} \\d{2}:\\d{2}:\\d{2}\\. Soo du ((\\d{1,3}(,\\d{3})*)|(\\d*))(\\.|\\.\\d*)? (?<$REGEX_KEY_BALANCE_CURRENCY>\\w+)\\. Ref (?<$REGEX_KEY_TRANSACTION_NAME>.+)".toRegex()

    companion object {
        private const val REGEX_KEY_ACCOUNT_NUMBER = "accountNumber"
        private const val REGEX_KEY_BALANCE_CHANGE = "balanceChange"
        private const val REGEX_KEY_CURRENCY = "currency"
        private const val REGEX_KEY_BALANCE_CURRENCY = "balanceCurrency"
        private const val REGEX_KEY_TRANSACTION_NAME = "transactionName"
    }
}