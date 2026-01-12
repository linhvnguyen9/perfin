package com.linh.perfin.domain.model.account

import com.linh.perfin.domain.model.BankInVietnam
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalTime::class)
data class Account(
    val id: String = generateId(),
    val name: String,
    val accountNumber: String = "",
    val bank: BankInVietnam?
) {
    companion object {
        @OptIn(ExperimentalUuidApi::class)
        fun generateId(): String = Uuid.random().toString()
    }
}

enum class AccountType(val displayName: String) {
    CHECKING("Checking"),
    SAVINGS("Savings"),
    CREDIT_CARD("Credit Card"),
    CASH("Cash"),
    INVESTMENT("Investment"),
    LOAN("Loan"),
    MORTGAGE("Mortgage"),
    OTHER("Other");

    companion object {
        fun fromString(value: String): AccountType {
            return values().find { it.name == value || it.displayName == value } ?: OTHER
        }
    }
}

enum class AccountGroup(val displayName: String) {
    PERSONAL("Personal"),
    BUSINESS("Business"),
    FAMILY("Family"),
    JOINT("Joint"),
    OTHER("Other");

    companion object {
        fun fromString(value: String?): AccountGroup? {
            if (value == null) return null
            return values().find { it.name == value || it.displayName == value } ?: OTHER
        }
    }
}

enum class CurrencyCode(val displayName: String) {
    VND("VND - Vietnamese Dong")
}