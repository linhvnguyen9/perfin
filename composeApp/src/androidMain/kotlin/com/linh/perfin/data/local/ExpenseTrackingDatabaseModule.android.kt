package com.linh.perfin.data.local

import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.perfin.features.expensetracking.ExpenseTrackingDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

actual val expenseTrackingDatabaseModule: Module = module {
    single<ExpenseTrackingDatabase> {
        val driver = AndroidSqliteDriver(
            schema = ExpenseTrackingDatabase.Schema,
            context = get(),
            name = EXPENSE_TRACKING_DATABASE_NAME,
            callback = object : AndroidSqliteDriver.Callback(ExpenseTrackingDatabase.Schema) {
                override fun onConfigure(db: SupportSQLiteDatabase) {
                    db.setForeignKeyConstraintsEnabled(true)
                }
            }
        )

        ExpenseTrackingDatabase(driver = driver)
    }
}