package com.linh.perfin.domain.usecase.banknotification.parsers

import com.linh.perfin.domain.model.transaction.Transaction
import com.linh.perfin.domain.model.transaction.TransactionType
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class TechcombankNotificationParser: NotificationParser {
    override fun isFromApp(packageName: String): Boolean = packageName == "vn.com.techcombank.bb.app"

    override fun parseNotification(title: String, text: String): Transaction? {
        val titleMatch = titleRegex.find(title)
        val titleMatchGroups = (titleMatch?.groups as? MatchNamedGroupCollection) ?: return null

        val transactionValue = titleMatchGroups[REGEX_KEY_BALANCE_CHANGE]?.value.orEmpty()
            .replace(",", "")

        val textSections = text.split("\n")

        val account = textSections[0].trim().replace("Account: ", "")
        val balanceMatch = balanceRegex.find(textSections[1].trim())
        val balanceMatchGroups = (balanceMatch?.groups as? MatchNamedGroupCollection) ?: return null
        val balance = balanceMatchGroups[REGEX_KEY_BALANCE]?.value.orEmpty()
        val transactionName = textSections[2].trim()

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

    private val titleRegex = "(?<$REGEX_KEY_TRANSACTION_TYPE>[+-]) (?<$REGEX_KEY_CURRENCY>\\w+) (?<$REGEX_KEY_BALANCE_CHANGE>((\\d{1,3}(,\\d{3})*)|(\\d*))(\\.|\\.\\d*)?)".toRegex()
    private val balanceRegex = "Balance: (?<$REGEX_KEY_CURRENCY>\\w+) (?<$REGEX_KEY_BALANCE>((\\d{1,3}(,\\d{3})*)|(\\d*))(\\.|\\.\\d*)?)".toRegex()

    companion object {
        private const val REGEX_KEY_BALANCE_CHANGE = "balanceChange"
        private const val REGEX_KEY_BALANCE = "balance"
        private const val REGEX_KEY_TRANSACTION_TYPE = "transactionType"
        private const val REGEX_KEY_CURRENCY = "currency"
    }
}