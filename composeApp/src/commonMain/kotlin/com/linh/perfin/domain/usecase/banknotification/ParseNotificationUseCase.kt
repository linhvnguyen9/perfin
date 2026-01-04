package com.linh.perfin.domain.usecase.banknotification

import com.linh.perfin.common.utils.NotificationAction
import com.linh.perfin.common.utils.NotificationUtils
import com.linh.perfin.domain.model.banknotification.Notification
import com.linh.perfin.domain.repository.banknotification.NotificationTrackerRepository
import com.linh.perfin.domain.repository.account.AccountRepository
import com.linh.perfin.domain.usecase.account.GetAllAccountsUseCase
import com.linh.perfin.domain.usecase.banknotification.parsers.NotificationParser
import com.linh.perfin.domain.usecase.transaction.CreateTransactionUseCase
import io.github.aakira.napier.Napier

class ParseNotificationUseCase(
    private val notificationTrackerRepository: NotificationTrackerRepository,
    private val createTransactionUseCase: CreateTransactionUseCase,
    private val getAllAccountsUseCase: GetAllAccountsUseCase,
    private val accountRepository: AccountRepository,
    private val parsers: List<NotificationParser>
) {
    suspend operator fun invoke(notification: Notification) {
        val isNotificationParsed = notificationTrackerRepository.isParsed(notification.timestamp, notification.packageName)
        if(isNotificationParsed) return

        notificationTrackerRepository.saveParseReceipt(notification.timestamp, notification.packageName)

        val parser = parsers.find { it.isFromApp(notification.packageName) } ?: run {
            Napier.e("No suitable parser found for ${notification.packageName}")
            return
        }

        val transaction = parser.parseNotification(notification.title, notification.text)
        if (transaction == null) {
            Napier.e("Failed to parse transaction $notification")
            return
        }
        
        // For now, we'll use the first available account
        // TODO: Implement proper account matching based on parsed transaction data
        val accounts = getAllAccountsUseCase()
        if (accounts.isEmpty()) {
            Napier.e("No accounts available to assign transaction")
            return
        }
        
        val targetAccount = accounts.first()
        val transactionWithAccount = transaction.copy(accountId = targetAccount.id)

        createTransactionUseCase(transactionWithAccount)

        // Send notification with Reply action to allow adding description
        NotificationUtils.sendNotification(
            title = "New Transaction Added",
            message = "Transaction of ${transactionWithAccount.amount} added to ${targetAccount.name}",
            channelId = "transaction_notifications",
            notificationId = transactionWithAccount.id.hashCode(),
            actions = listOf(
                NotificationAction.Reply(
                    title = "Add Description",
                    action = "com.linh.perfin.ADD_TRANSACTION_DESCRIPTION",
                    replyLabel = "Enter transaction description..."
                ),
                NotificationAction.Button(
                    title = "View",
                    action = "com.linh.perfin.VIEW_TRANSACTION"
                )
            )
        )
    }
}