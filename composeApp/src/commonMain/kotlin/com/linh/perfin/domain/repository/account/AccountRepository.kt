package com.linh.perfin.domain.repository.account

import com.linh.perfin.domain.model.account.Account
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    suspend fun getAllAccounts(): List<Account>
    fun observeAllAccounts(): Flow<List<Account>>
    suspend fun getAccountById(id: String): Account?
    suspend fun createAccount(account: Account)
    suspend fun updateAccount(account: Account)
    suspend fun deleteAccount(id: String)
}