package com.linh.perfin.data.local.account

import com.perfin.features.expensetracking.AccountEntity
import com.perfin.features.expensetracking.ExpenseTrackingDatabase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
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

    override suspend fun insertAccount(account: AccountEntity) {
        return withContext(ioDispatcher) {
            queries.insertAccount(
                account_id = account.account_id,
                name = account.name,
                bank = account.bank
            )
        }
    }
}