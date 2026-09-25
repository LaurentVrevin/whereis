package com.laurentvrevin.wheris

import android.app.Application
import com.laurentvrevin.wheris.core.database.di.databaseModule
import com.laurentvrevin.wheris.core.location.di.locationModule
import com.laurentvrevin.wheris.data.di.dataModule
import com.laurentvrevin.wheris.feature.addpin.di.addPinModule
import com.laurentvrevin.wheris.feature.home.di.homeModule
import com.laurentvrevin.wheris.feature.pindetail.di.pinDetailModule
import com.laurentvrevin.wheris.feature.pins.di.pinsModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            if (BuildConfig.DEBUG) {
                androidLogger(Level.DEBUG)
            }
            androidContext(this@App)
            modules(
                databaseModule,
                dataModule,
                locationModule,
                addPinModule,
                homeModule,
                pinsModule,
                pinDetailModule,
            )
        }
    }
}
