package com.linh.perfin.domain.model.banknotification

data class Notification(
    val title: String,
    val text: String,
    val timestamp: Long,
    val packageName: String
)