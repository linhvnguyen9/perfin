package com.linh.perfin

import androidx.compose.ui.window.ComposeUIViewController
import com.linh.perfin.di.startKoinForPerfin
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

fun initKoin() {
    Napier.base(DebugAntilog())
    startKoinForPerfin()
}

fun MainViewController() = ComposeUIViewController { App() }