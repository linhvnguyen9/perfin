package com.linh.perfin.data.local

import app.cash.sqldelight.driver.native.NativeSqliteDriver
import co.touchlab.sqliter.DatabaseConfiguration
import com.perfin.features.expensetracking.ExpenseTrackingDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

actual val expenseTrackingDatabaseModule: Module = module {
    single<ExpenseTrackingDatabase> {
        val driver = NativeSqliteDriver(
            ExpenseTrackingDatabase.Schema,
            EXPENSE_TRACKING_DATABASE_NAME,
            onConfiguration = { configuration ->
                configuration.copy(
                    extendedConfig = DatabaseConfiguration.Extended(
                        foreignKeyConstraints = true
                    )
                )
            }
        )

        ExpenseTrackingDatabase(driver = driver)
    }
}