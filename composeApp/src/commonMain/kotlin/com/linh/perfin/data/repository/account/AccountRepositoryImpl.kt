package com.linh.perfin.data.repository.account

import com.linh.perfin.data.local.account.AccountLocalDataSource
import com.linh.perfin.data.repository.account.mapper.toDomain
import com.linh.perfin.data.repository.account.mapper.toEntity
import com.linh.perfin.domain.model.account.Account
import com.linh.perfin.domain.repository.account.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AccountRepositoryImpl(
    private val localDataSource: AccountLocalDataSource
): AccountRepository {

    override suspend fun getAllAccounts(): List<Account> {
        return localDataSource.getAllAccounts().map { it.toDomain() }
    }

    override fun observeAllAccounts(): Flow<List<Account>> {
        return localDataSource.observeAllAccounts().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getAccountById(id: String): Account? {
        return localDataSource.getAccountById(id)?.toDomain()
    }

    override suspend fun createAccount(account: Account) {
        localDataSource.insertAccount(account.toEntity())
    }

    override suspend fun updateAccount(account: Account) {
        localDataSource.updateAccount(account.toEntity())
    }

    override suspend fun deleteAccount(id: String) {
        localDataSource.deleteAccount(id)
    }
}