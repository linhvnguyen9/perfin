package com.linh.perfin.data.repository.account.mapper

import com.linh.perfin.domain.model.BankInVietnam
import com.linh.perfin.domain.model.account.Account
import com.perfin.features.expensetracking.AccountEntity

fun AccountEntity.toDomain(): Account {
    return Account(
        id = account_id,
        name = name,
        bank = bank?.let { BankInVietnam.valueOf(it) }
    )
}

fun Account.toEntity(): AccountEntity {
    return AccountEntity(
        account_id = id,
        name = name,
        bank = bank?.name
    )
}

