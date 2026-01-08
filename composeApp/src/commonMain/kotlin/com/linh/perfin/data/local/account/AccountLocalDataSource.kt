package com.linh.perfin.data.local.account

import com.perfin.features.expensetracking.AccountEntity
import kotlinx.coroutines.flow.Flow

interface AccountLocalDataSource {
    suspend fun getAllAccounts(): List<AccountEntity>
    fun observeAllAccounts(): Flow<List<AccountEntity>>
    suspend fun getAccountById(id: String): AccountEntity?
    suspend fun insertAccount(account: AccountEntity)
    suspend fun updateAccount(account: AccountEntity)
    suspend fun deleteAccount(id: String)
}