package com.linh.perfin.data.local

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.perfin.features.expensetracking.ExpenseTrackingDatabase
import org.koin.core.module.Module
import org.koin.dsl.module
import java.nio.file.Files
import java.nio.file.Paths

actual val expenseTrackingDatabaseModule: Module = module {
    single<ExpenseTrackingDatabase> {
        val databaseName = EXPENSE_TRACKING_DATABASE_NAME
        val databasePath = Paths.get(System.getProperty("user.home"), "perfin").toAbsolutePath()
        if (!Files.exists(databasePath) || !Files.isDirectory(databasePath)) {
            Files.createDirectory(databasePath)
        }
        val jdbcUrl = "jdbc:sqlite:$databasePath/$databaseName"
        val driver = JdbcSqliteDriver(jdbcUrl).also {
            it.execute(null, "PRAGMA foreign_keys = ON", 0)
            ExpenseTrackingDatabase.Schema.create(it)
        }

        ExpenseTrackingDatabase(driver)
    }
}