package com.troves.di

import com.troves.data.platform.connectivity.ConnectivityObserverFactory
import com.troves.data.source.local.database.DatabaseFactory
import com.troves.data.local.preferenceses.createDataStore
import com.troves.domain.utils.connectivity.ConnectivityObserver
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single { createDataStore() }
    single { DatabaseFactory() }
    single { ConnectivityObserverFactory() }
    single<ConnectivityObserver> { get<ConnectivityObserverFactory>().create() }
}
