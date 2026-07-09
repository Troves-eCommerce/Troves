package com.troves

import android.app.Application
import com.troves.data.config.MapboxConfig
import com.mapbox.common.MapboxOptions
import com.troves.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class TrovesApp : Application() {
    override fun onCreate() {
        super.onCreate()

        MapboxOptions.accessToken = MapboxConfig.ACCESS_TOKEN

        initKoin {
            androidLogger()
            androidContext(this@TrovesApp)
        }
    }
}
