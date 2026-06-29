package com.troves

import android.app.Application
import com.troves.di.initKoin

class TrovesApp: Application() {
    companion object{
        lateinit var trovesApplication: Application
    }

    override fun onCreate() {
        super.onCreate()
        trovesApplication = this
        initKoin()
    }
}
