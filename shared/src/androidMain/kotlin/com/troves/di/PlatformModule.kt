package com.troves.di

import com.troves.data.platform.connectivity.ConnectivityObserverFactory
import com.troves.data.source.local.database.DatabaseFactory
import com.troves.data.source.local.preferenceses.createDataStore
import com.troves.domain.utils.connectivity.ConnectivityObserver
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single { createDataStore(androidContext()) }
    single { DatabaseFactory(androidContext()) }
    single { ConnectivityObserverFactory(androidContext()) }
    single<ConnectivityObserver> { get<ConnectivityObserverFactory>().create() }
}