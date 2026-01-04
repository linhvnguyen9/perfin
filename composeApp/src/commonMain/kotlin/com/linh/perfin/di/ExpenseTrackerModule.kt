package com.linh.perfin.di

import com.linh.perfin.data.local.account.AccountLocalDataSource
import com.linh.perfin.data.local.account.AccountLocalDataSourceImpl
import com.linh.perfin.data.local.banknotification.BankNotificationLocalDataSource
import com.linh.perfin.data.local.banknotification.BankNotificationLocalDataSourceImpl
import com.linh.perfin.data.local.expenseTrackingDatabaseModule
import com.linh.perfin.data.local.transaction.TransactionLocalDataSource
import com.linh.perfin.data.local.transaction.TransactionLocalDataSourceImpl
import com.linh.perfin.data.repository.account.AccountRepositoryImpl
import com.linh.perfin.data.repository.banknotification.NotificationTrackerRepositoryImpl
import com.linh.perfin.data.repository.transaction.TransactionRepositoryImpl
import com.linh.perfin.domain.repository.account.AccountRepository
import com.linh.perfin.domain.repository.banknotification.NotificationTrackerRepository
import com.linh.perfin.domain.repository.category.CategoryRepository
import com.linh.perfin.domain.repository.transaction.TransactionRepository
import com.linh.perfin.domain.usecase.account.CreateAccountUseCase
import com.linh.perfin.domain.usecase.account.GetAllAccountsUseCase
import com.linh.perfin.domain.usecase.banknotification.ParseNotificationUseCase
import com.linh.perfin.domain.usecase.banknotification.parsers.NotificationParser
import com.linh.perfin.domain.usecase.banknotification.parsers.TechcombankNotificationParser
import com.linh.perfin.domain.usecase.banknotification.parsers.VietcombankNotificationParser
import com.linh.perfin.domain.usecase.transaction.CreateTransactionUseCase
import com.linh.perfin.domain.usecase.transaction.GetAllTransactionsUseCase
import com.linh.perfin.domain.usecase.transaction.GetTransactionsWithDetailsUseCase
import com.linh.perfin.domain.usecase.transaction.UpdateTransactionNameUseCase
import org.koin.core.qualifier.named
import org.koin.dsl.module

val expenseTrackingModule = module {
    includes(expenseTrackingDatabaseModule)

    factory<AccountLocalDataSource> { AccountLocalDataSourceImpl(get()) }
    factory<TransactionLocalDataSource> { TransactionLocalDataSourceImpl(get()) }
    factory<BankNotificationLocalDataSource> { BankNotificationLocalDataSourceImpl(get()) }

    factory<AccountRepository> { AccountRepositoryImpl(get()) }
    factory<TransactionRepository> { TransactionRepositoryImpl(get()) }
    factory<NotificationTrackerRepository> { NotificationTrackerRepositoryImpl(get()) }

    factory { CreateAccountUseCase(get()) }
    factory { GetAllAccountsUseCase(get()) }
    factory { GetAllTransactionsUseCase(get()) }
    factory { CreateTransactionUseCase(get()) }
    factory { UpdateTransactionNameUseCase(get()) }
    factory { ParseNotificationUseCase(get(), get(), get(), get(), getAll<NotificationParser>()) }
    factory { GetTransactionsWithDetailsUseCase(get(), get(), get()) }

    single<NotificationParser>(named("VietcombankNotificationParser")) { VietcombankNotificationParser() }
    single<NotificationParser>(named("TechcombankNotificationParser")) { TechcombankNotificationParser() }
}