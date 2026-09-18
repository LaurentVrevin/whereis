package com.laurentvrevin.wheris

import android.app.Application
import com.laurentvrevin.wheris.data.di.dataModule
import com.laurentvrevin.wheris.core.database.di.databaseModule
import com.laurentvrevin.wheris.core.datastore.di.datastoreModule
import com.laurentvrevin.wheris.di.appModule
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
                databaseModule,
                datastoreModule,
                dataModule,
                appModule,
            )
        }
    }
}
