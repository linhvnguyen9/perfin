package com.linh.perfin.data.local.account

import com.perfin.features.expensetracking.AccountEntity

interface AccountLocalDataSource {
    suspend fun getAllAccounts(): List<AccountEntity>
    suspend fun insertAccount(account: AccountEntity)
}