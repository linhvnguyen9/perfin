package com.linh.perfin.presentation.account.addedit.models

import com.linh.perfin.domain.model.BankInVietnam
import com.linh.perfin.domain.model.account.Account

data class AccountFormData(
    val accountId: String?,
    val name: String,
    val bank: BankInVietnam?
) {
    fun toAccount(): Account {
        return Account(
            id = accountId ?: Account.generateId(),
            name = name,
            bank = bank
        )
    }

    companion object {
        fun fromAccount(account: Account): AccountFormData {
            return AccountFormData(
                accountId = account.id,
                name = account.name,
                bank = account.bank
            )
        }

        fun empty(): AccountFormData {
            return AccountFormData(
                accountId = null,
                name = "",
                bank = null
            )
        }
    }
}
