package com.troves.di

import com.troves.data.local.database.DatabaseFactory
import com.troves.data.local.preferenceses.createDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single { createDataStore(androidContext()) }
    single { DatabaseFactory(androidContext()) }
    single { createDataStore(androidContext()) }
}
