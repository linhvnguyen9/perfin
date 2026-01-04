package com.linh.perfin.domain.usecase.banknotification.parsers

import com.linh.perfin.domain.model.transaction.Transaction

interface NotificationParser {
    fun isFromApp(packageName: String): Boolean
    fun parseNotification(title: String, text: String): Transaction?
}