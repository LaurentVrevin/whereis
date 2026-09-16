package com.laurentvrevin.androidstarter

import android.app.Application
import com.laurentvrevin.androidstarter.data.di.dataModule
import com.laurentvrevin.androidstarter.data.di.networkModule
import com.laurentvrevin.androidstarter.designsystem.di.designSystemModule
import com.laurentvrevin.androidstarter.di.appModule
import com.laurentvrevin.androidstarter.di.configurationModule
import com.laurentvrevin.androidstarter.feature.template.di.templateModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(
                configurationModule,
                networkModule,
                dataModule,
                designSystemModule,
                templateModule,
                appModule,
            )
        }
    }
}
