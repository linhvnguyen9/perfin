package com.linh.perfin.di

import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration

fun startKoinForPerfin(
    appDeclaration: KoinAppDeclaration = {}
) {
    startKoin {
        appDeclaration()
        modules(sharedModule)
    }
}

val sharedModule: List<Module> = listOf(
    expenseTrackingModule
)