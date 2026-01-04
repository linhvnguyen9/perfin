package com.linh.perfin.common.utils

import platform.UserNotifications.*

actual object NotificationUtils {
    
    fun initialize() {
        val center = UNUserNotificationCenter.currentNotificationCenter()
        center.requestAuthorizationWithOptions(
            UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge
        ) { granted, error ->
            if (!granted) {
                println("Notification permission denied: ${error?.localizedDescription}")
            }
        }
    }
    
    actual fun sendNotification(
        title: String,
        message: String,
        channelId: String?,
        notificationId: Int,
        onClickAction: String?,
        isOngoing: Boolean,
        actions: List<NotificationAction>,
        extras: Map<String, String>
    ) {
        val center = UNUserNotificationCenter.currentNotificationCenter()
        
        val content = UNMutableNotificationContent().apply {
            setTitle(title)
            setBody(message)
            setSound(UNNotificationSound.defaultSound)
        }
        
        // Add actions to the notification
        if (actions.isNotEmpty()) {
            val notificationActions = actions.map { action ->
                when (action) {
                    is NotificationAction.Button -> {
                        UNNotificationAction.actionWithIdentifier(
                            identifier = action.action,
                            title = action.title,
                            options = UNNotificationActionOptionNone
                        )
                    }
                    
                    is NotificationAction.Reply -> {
                        UNTextInputNotificationAction.actionWithIdentifier(
                            identifier = action.action,
                            title = action.title,
                            options = UNNotificationActionOptionNone,
                            textInputButtonTitle = "Send",
                            textInputPlaceholder = action.replyLabel
                        )
                    }
                }
            }
            
            // Create category with actions
            val categoryIdentifier = "TRANSACTION_CATEGORY_${notificationId}"
            val category = UNNotificationCategory.categoryWithIdentifier(
                identifier = categoryIdentifier,
                actions = notificationActions,
                intentIdentifiers = emptyList<String>(),
                options = UNNotificationCategoryOptionNone
            )
            
            center.setNotificationCategories(setOf(category))
            content.setCategoryIdentifier(categoryIdentifier)
        }
        
        // Create trigger - immediate delivery
        val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(
            timeInterval = 0.1,
            repeats = false
        )
        
        // Create request
        val request = UNNotificationRequest.requestWithIdentifier(
            identifier = notificationId.toString(),
            content = content,
            trigger = trigger
        )
        
        // Schedule notification
        center.addNotificationRequest(request) { error ->
            error?.let {
                println("Error scheduling notification: ${it.localizedDescription}")
            }
        }
    }
}