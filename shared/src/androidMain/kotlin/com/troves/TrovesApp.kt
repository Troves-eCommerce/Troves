package com.troves

import android.app.Application
import com.troves.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class TrovesApp: Application() {
    companion object{
        lateinit var trovesApplication: Application
    }

    override fun onCreate() {
        super.onCreate()
        trovesApplication = this
        initKoin {
            androidLogger()
            androidContext(this@TrovesApp)
        }
    }
}
