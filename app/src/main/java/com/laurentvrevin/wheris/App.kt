package com.laurentvrevin.wheris

import android.app.Application
import com.laurentvrevin.wheris.data.di.dataModule
import com.laurentvrevin.wheris.data.di.networkModule
import com.laurentvrevin.wheris.designsystem.di.designSystemModule
import com.laurentvrevin.wheris.di.appModule
import com.laurentvrevin.wheris.di.configurationModule
import com.laurentvrevin.wheris.feature.template.di.templateModule
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
