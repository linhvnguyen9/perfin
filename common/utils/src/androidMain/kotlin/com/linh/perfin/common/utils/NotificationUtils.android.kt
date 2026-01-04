package com.linh.perfin.common.utils

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import androidx.core.content.ContextCompat

@SuppressLint("StaticFieldLeak")
actual object NotificationUtils {
    private lateinit var context: Context
    private lateinit var notificationManager: NotificationManager
    
    fun initialize(context: Context) {
        this.context = context
        this.notificationManager = ContextCompat.getSystemService(context, NotificationManager::class.java)!!
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
        val effectiveChannelId = channelId ?: "default_channel"
        
        createNotificationChannelIfNotExists(effectiveChannelId)
        
        val builder = NotificationCompat.Builder(context, effectiveChannelId)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setOngoing(isOngoing)
            .setAutoCancel(!isOngoing)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
        
        onClickAction?.let { action ->
            val clickIntent = Intent(action).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val clickPendingIntent = PendingIntent.getActivity(
                context,
                notificationId,
                clickIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            builder.setContentIntent(clickPendingIntent)
        }
        
        actions.forEach { action ->
            when (action) {
                is NotificationAction.Button -> {
                    val actionIntent = Intent(action.action).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    val actionPendingIntent = PendingIntent.getBroadcast(
                        context,
                        action.action.hashCode(),
                        actionIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    
                    val notificationAction = if (action.icon != null) {
                        NotificationCompat.Action(action.icon, action.title, actionPendingIntent)
                    } else {
                        NotificationCompat.Action(0, action.title, actionPendingIntent)
                    }
                    
                    builder.addAction(notificationAction)
                }
                
                is NotificationAction.Reply -> {
                    val remoteInput = RemoteInput.Builder("reply_text")
                        .setLabel(action.replyLabel)
                        .build()
                    
                    val replyIntent = Intent().apply {
                        setClassName(context, "com.linh.perfin.device.notification.NotificationReplyBroadcastReceiver")
                        this.action = action.action
                        putExtra("notification_id", notificationId)
                        extras.forEach { (key, value) ->
                            putExtra(key, value)
                        }
                    }
                    val replyPendingIntent = PendingIntent.getBroadcast(
                        context,
                        action.action.hashCode(),
                        replyIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                    )
                    
                    val replyAction = NotificationCompat.Action.Builder(
                        action.icon ?: 0,
                        action.title,
                        replyPendingIntent
                    )
                        .addRemoteInput(remoteInput)
                        .build()
                    
                    builder.addAction(replyAction)
                }
            }
        }
        
        notificationManager.notify(notificationId, builder.build())
    }
    
    private fun createNotificationChannelIfNotExists(channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val existingChannel = notificationManager.getNotificationChannel(channelId)
            if (existingChannel == null) {
                val channelName = when (channelId) {
                    "default_channel" -> "Default Notifications"
                    "fcm_default_channel" -> "Default Channel"
                    else -> "App Notifications"
                }
                
                val channel = NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Notifications from the app"
                }
                
                notificationManager.createNotificationChannel(channel)
            }
        }
    }
}