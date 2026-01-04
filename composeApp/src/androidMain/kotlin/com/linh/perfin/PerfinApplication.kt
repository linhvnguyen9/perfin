package com.linh.perfin

import android.app.Application
import com.linh.perfin.common.utils.NotificationUtils
import com.linh.perfin.di.startKoinForPerfin
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext

class PerfinApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        Napier.base(DebugAntilog())

        startKoinForPerfin {
            androidContext(this@PerfinApplication)
//            NotificationInitializer.onApplicationStart()
        }

//        initNotificationListener()
        NotificationUtils.initialize(this)
    }
}