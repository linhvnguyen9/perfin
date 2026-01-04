package com.linh.perfin.data.local.banknotification

import com.perfin.features.expensetracking.NotificationParsedReceiptEntity

interface BankNotificationLocalDataSource {
    suspend fun getReceipt(timestamp: Long, packageName: String): NotificationParsedReceiptEntity?
    suspend fun insertReceipt(receipt: NotificationParsedReceiptEntity)
}