package com.linh.perfin.di

import com.linh.perfin.data.local.account.AccountLocalDataSource
import com.linh.perfin.data.local.account.AccountLocalDataSourceImpl
import com.linh.perfin.data.local.banknotification.BankNotificationLocalDataSource
import com.linh.perfin.data.local.banknotification.BankNotificationLocalDataSourceImpl
import com.linh.perfin.data.local.category.CategoryLocalDataSource
import com.linh.perfin.data.local.category.CategoryLocalDataSourceImpl
import com.linh.perfin.data.local.expenseTrackingDatabaseModule
import com.linh.perfin.data.local.transaction.TransactionLocalDataSource
import com.linh.perfin.data.local.transaction.TransactionLocalDataSourceImpl
import com.linh.perfin.data.repository.account.AccountRepositoryImpl
import com.linh.perfin.data.repository.banknotification.NotificationTrackerRepositoryImpl
import com.linh.perfin.data.repository.category.CategoryRepositoryImpl
import com.linh.perfin.data.repository.transaction.TransactionRepositoryImpl
import com.linh.perfin.domain.repository.account.AccountRepository
import com.linh.perfin.domain.repository.banknotification.NotificationTrackerRepository
import com.linh.perfin.domain.repository.category.CategoryRepository
import com.linh.perfin.domain.repository.transaction.TransactionRepository
import com.linh.perfin.domain.usecase.account.CreateAccountUseCase
import com.linh.perfin.domain.usecase.account.DeleteAccountUseCase
import com.linh.perfin.domain.usecase.account.GetAccountByIdUseCase
import com.linh.perfin.domain.usecase.account.GetAllAccountsUseCase
import com.linh.perfin.domain.usecase.account.UpdateAccountUseCase
import com.linh.perfin.domain.usecase.banknotification.ParseNotificationUseCase
import com.linh.perfin.domain.usecase.banknotification.parsers.NotificationParser
import com.linh.perfin.domain.usecase.banknotification.parsers.TechcombankNotificationParser
import com.linh.perfin.domain.usecase.banknotification.parsers.VietcombankNotificationParser
import com.linh.perfin.domain.usecase.transaction.CreateTransactionUseCase
import com.linh.perfin.domain.usecase.transaction.GetAllTransactionsUseCase
import com.linh.perfin.domain.usecase.transaction.GetTransactionsWithDetailsUseCase
import com.linh.perfin.domain.usecase.transaction.UpdateTransactionNameUseCase
import com.linh.perfin.presentation.account.addedit.AccountAddEditViewModel
import com.linh.perfin.presentation.account.addedit.AccountFormValidator
import com.linh.perfin.presentation.account.list.ManageAccountsViewModel
import com.linh.perfin.presentation.profile.ProfileViewModel
import com.linh.perfin.presentation.transaction.addedit.TransactionAddEditViewModel
import com.linh.perfin.presentation.transaction.addedit.TransactionFormValidator
import com.linh.perfin.presentation.transaction.list.TransactionListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val expenseTrackingModule = module {
    includes(expenseTrackingDatabaseModule)

    factory<AccountLocalDataSource> { AccountLocalDataSourceImpl(get()) }
    factory<TransactionLocalDataSource> { TransactionLocalDataSourceImpl(get()) }
    factory<BankNotificationLocalDataSource> { BankNotificationLocalDataSourceImpl(get()) }
    factory<CategoryLocalDataSource> { CategoryLocalDataSourceImpl(get()) }

    factory<AccountRepository> { AccountRepositoryImpl(get()) }
    factory<TransactionRepository> { TransactionRepositoryImpl(get()) }
    factory<NotificationTrackerRepository> { NotificationTrackerRepositoryImpl(get()) }
    factory<CategoryRepository> { CategoryRepositoryImpl(get()) }

    factory { CreateAccountUseCase(get()) }
    factory { GetAllAccountsUseCase(get()) }
    factory { GetAccountByIdUseCase(get()) }
    factory { UpdateAccountUseCase(get()) }
    factory { DeleteAccountUseCase(get(), get()) }
    factory { GetAllTransactionsUseCase(get()) }
    factory { CreateTransactionUseCase(get()) }
    factory { UpdateTransactionNameUseCase(get()) }
    factory { ParseNotificationUseCase(get(), get(), get(), get(), getAll<NotificationParser>()) }
    factory { GetTransactionsWithDetailsUseCase(get(), get(), get()) }

    viewModel { TransactionListViewModel(get()) }

    viewModel { params ->
        TransactionAddEditViewModel(
            transactionId = params.getOrNull(),
            createTransactionUseCase = get(),
            getAllAccountsUseCase = get(),
            categoryRepository = get(),
            transactionRepository = get(),
            validator = get()
        )
    }

    viewModel { ProfileViewModel() }

    viewModel { ManageAccountsViewModel(get()) }

    viewModel { params ->
        AccountAddEditViewModel(
            accountId = params.getOrNull(),
            createAccountUseCase = get(),
            getAccountByIdUseCase = get(),
            updateAccountUseCase = get(),
            deleteAccountUseCase = get(),
            validator = get()
        )
    }

    single { TransactionFormValidator() }
    single { AccountFormValidator() }

    single<NotificationParser>(named("VietcombankNotificationParser")) { VietcombankNotificationParser() }
    single<NotificationParser>(named("TechcombankNotificationParser")) { TechcombankNotificationParser() }
}