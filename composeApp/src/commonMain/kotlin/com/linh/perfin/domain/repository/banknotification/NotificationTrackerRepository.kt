package com.linh.perfin.domain.repository.banknotification

interface NotificationTrackerRepository {
    suspend fun isParsed(timestamp: Long, packageName: String): Boolean
    suspend fun saveParseReceipt(timestamp: Long, packageName: String)
}