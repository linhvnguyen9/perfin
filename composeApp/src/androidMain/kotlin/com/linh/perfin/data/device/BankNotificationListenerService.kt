package com.linh.perfin.data.device

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.linh.perfin.domain.model.banknotification.Notification as BankNotification
import com.linh.perfin.domain.usecase.banknotification.ParseNotificationUseCase
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class BankNotificationListenerService: NotificationListenerService(), KoinComponent {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    private val parseNotificationUseCase: ParseNotificationUseCase by inject()

    override fun onListenerConnected() {
        super.onListenerConnected()

        Napier.d("Listener connected")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)

        val title = sbn?.notification?.extras?.get(Notification.EXTRA_TITLE).toString()
        val text = sbn?.notification?.extras?.get(Notification.EXTRA_TEXT).toString()
        val packageName = sbn?.packageName.orEmpty()
        val timestamp = sbn?.postTime ?: 0L

        Napier.d("onNotificationPosted packageName $packageName title $title text $text id ${sbn?.id} time $timestamp")

        scope.launch {
            parseNotificationUseCase(BankNotification(title, text, timestamp, packageName))
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)

        Napier.d("onNotificationRemoved packageName ${sbn?.packageName} title ${sbn?.notification?.extras?.get(
            Notification.EXTRA_TITLE)} text ${sbn?.notification?.extras?.get(Notification.EXTRA_TEXT)} id ${sbn?.id} time ${sbn?.postTime}")
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()

        Napier.d("Listener disconnected")

        scope.cancel()
    }
}