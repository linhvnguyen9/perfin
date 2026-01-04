package com.linh.perfin.domain.repository.account

import com.linh.perfin.domain.model.account.Account

interface AccountRepository {
    suspend fun getAllAccounts(): List<Account>
    suspend fun createAccount(account: Account)
}