package com.troves.di

import com.example.data.di.dataModule
import com.example.domain.di.domainModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

/**
 * Initialises Koin for the whole app.
 * Call once — from the Android Application class or iOS entry point.
 */
fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        appDeclaration()
        modules(
            dataModule,
            domainModule,
            platformModule()
        )
    }
}