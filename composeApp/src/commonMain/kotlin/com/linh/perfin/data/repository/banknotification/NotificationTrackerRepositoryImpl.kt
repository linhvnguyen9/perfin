package com.linh.perfin.data.repository.banknotification

import com.linh.perfin.data.local.banknotification.BankNotificationLocalDataSource
import com.linh.perfin.domain.repository.banknotification.NotificationTrackerRepository
import com.perfin.features.expensetracking.NotificationParsedReceiptEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class NotificationTrackerRepositoryImpl(
    private val bankNotificationLocalDataSource: BankNotificationLocalDataSource
) : NotificationTrackerRepository {
    override suspend fun isParsed(timestamp: Long, packageName: String): Boolean =
        withContext(Dispatchers.IO) {
            return@withContext bankNotificationLocalDataSource.getReceipt(
                timestamp,
                packageName
            ) != null
        }

    override suspend fun saveParseReceipt(timestamp: Long, packageName: String) =
        withContext(Dispatchers.IO) {
            bankNotificationLocalDataSource.insertReceipt(
                NotificationParsedReceiptEntity(
                    timestamp,
                    packageName
                )
            )
        }
}