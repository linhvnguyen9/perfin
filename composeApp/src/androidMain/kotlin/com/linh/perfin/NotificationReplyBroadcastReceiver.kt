package com.linh.perfin

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput
import com.linh.perfin.domain.usecase.transaction.UpdateTransactionNameUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NotificationReplyBroadcastReceiver : BroadcastReceiver(), KoinComponent {

    private val updateTransactionNameUseCase: UpdateTransactionNameUseCase by inject()

    override fun onReceive(context: Context, intent: Intent) {
        val replyText = getMessageText(intent)?.toString()
        val action = intent.action
        val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, -1)

        if (replyText != null && action != null) {
            // Handle the reply text based on the action
            handleReply(context, intent, action, replyText, notificationId)

            // Optionally dismiss the notification
            if (notificationId != -1) {
                NotificationManagerCompat.from(context).cancel(notificationId)
            }
        }
    }

    private fun getMessageText(intent: Intent): CharSequence? {
        return RemoteInput.getResultsFromIntent(intent)?.getCharSequence("reply_text")
    }

    private fun handleReply(context: Context, intent: Intent, action: String, replyText: String, notificationId: Int) {
        // This is where you would handle different reply actions
        // You can use the action string to determine what to do with the reply

        when (action) {
            "com.linh.perfin.ADD_TRANSACTION_DESCRIPTION" -> {
                val transactionId = intent.getStringExtra("transactionId")
                if (transactionId != null) {
                    CoroutineScope(Dispatchers.IO).launch {
                        updateTransactionNameUseCase(
                            transactionId = transactionId,
                            name = replyText
                        )
                    }
                }
            }
            "REPLY_TO_CHAT" -> {
            }
            "REPLY_TO_EXPENSE" -> {
            }
            else -> {
            }
        }

        // You can also broadcast an internal intent to notify other parts of your app
        val replyIntent = Intent(ACTION_NOTIFICATION_REPLY).apply {
            putExtra(EXTRA_ACTION, action)
            putExtra(EXTRA_REPLY_TEXT, replyText)
            putExtra(EXTRA_NOTIFICATION_ID, notificationId)
        }
        context.sendBroadcast(replyIntent)
    }

    companion object {
        const val EXTRA_NOTIFICATION_ID = "notification_id"
        const val EXTRA_ACTION = "action"
        const val EXTRA_REPLY_TEXT = "reply_text"
        const val ACTION_NOTIFICATION_REPLY = "com.linh.perfin.NOTIFICATION_REPLY"
    }
}