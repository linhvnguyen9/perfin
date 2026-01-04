package com.linh.perfin.data.local.banknotification

import com.perfin.features.expensetracking.ExpenseTrackingDatabase
import com.perfin.features.expensetracking.NotificationParsedReceiptEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class BankNotificationLocalDataSourceImpl(
    database: ExpenseTrackingDatabase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): BankNotificationLocalDataSource {

    private val queries = database.notificationParsedReceiptQueries

    override suspend fun getReceipt(timestamp: Long, packageName: String): NotificationParsedReceiptEntity? {
        return withContext(ioDispatcher) {
            queries.getReceipt(timestamp, packageName).executeAsOneOrNull()
        }
    }

    override suspend fun insertReceipt(receipt: NotificationParsedReceiptEntity) {
        withContext(ioDispatcher) {
            queries.insert(
                timestamp = receipt.timestamp,
                package_name = receipt.package_name
            )
        }
    }
}