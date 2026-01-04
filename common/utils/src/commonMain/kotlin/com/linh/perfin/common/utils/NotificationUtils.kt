package com.linh.perfin.common.utils

sealed class NotificationAction {
    data class Button(
        val title: String,
        val action: String,
        val icon: Int? = null
    ) : NotificationAction()
    
    data class Reply(
        val title: String,
        val action: String,
        val replyLabel: String,
        val icon: Int? = null
    ) : NotificationAction()
}

expect object NotificationUtils {
    fun sendNotification(
        title: String,
        message: String,
        channelId: String? = null,
        notificationId: Int = 0,
        onClickAction: String? = null,
        isOngoing: Boolean = false,
        actions: List<NotificationAction> = emptyList(),
        extras: Map<String, String> = emptyMap()
    )
}