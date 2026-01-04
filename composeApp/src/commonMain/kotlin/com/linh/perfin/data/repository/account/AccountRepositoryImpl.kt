package com.linh.perfin.data.repository.account

import com.linh.perfin.data.local.account.AccountLocalDataSource
import com.linh.perfin.data.repository.account.mapper.toDomain
import com.linh.perfin.data.repository.account.mapper.toEntity
import com.linh.perfin.domain.model.account.Account
import com.linh.perfin.domain.repository.account.AccountRepository

class AccountRepositoryImpl(
    private val localDataSource: AccountLocalDataSource
): AccountRepository {

    override suspend fun getAllAccounts(): List<Account> {
        return localDataSource.getAllAccounts().map { it.toDomain() }
    }

    override suspend fun createAccount(account: Account) {
        localDataSource.insertAccount(account.toEntity())
    }
}