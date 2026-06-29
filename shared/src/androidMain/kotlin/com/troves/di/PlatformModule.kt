package com.troves.di

import TrovesApp.Companion.trovesApplication
import com.troves.data.local.preferenceses.createDataStoreAndroid
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single { createDataStoreAndroid(get()) }
}
