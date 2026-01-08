package com.linh.perfin.data.local.account

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.perfin.features.expensetracking.AccountEntity
import com.perfin.features.expensetracking.ExpenseTrackingDatabase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class AccountLocalDataSourceImpl(
    database: ExpenseTrackingDatabase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): AccountLocalDataSource {

    private val queries = database.accountQueries

    override suspend fun getAllAccounts(): List<AccountEntity> {
        return withContext(ioDispatcher) {
            queries.getAllAccounts().executeAsList()
        }
    }

    override fun observeAllAccounts(): Flow<List<AccountEntity>> {
        return queries.getAllAccounts()
            .asFlow()
            .mapToList(ioDispatcher)
    }

    override suspend fun getAccountById(id: String): AccountEntity? {
        return withContext(ioDispatcher) {
            queries.getAccountById(id).executeAsOneOrNull()
        }
    }

    override suspend fun insertAccount(account: AccountEntity) {
        return withContext(ioDispatcher) {
            queries.insertAccount(
                account_id = account.account_id,
                name = account.name,
                bank = account.bank
            )
        }
    }

    override suspend fun updateAccount(account: AccountEntity) {
        return withContext(ioDispatcher) {
            queries.updateAccount(
                account_id = account.account_id,
                name = account.name,
            )
        }
    }

    override suspend fun deleteAccount(id: String) {
        return withContext(ioDispatcher) {
            queries.deleteAccount(id)
        }
    }
}