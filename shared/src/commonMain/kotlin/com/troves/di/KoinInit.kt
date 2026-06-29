package com.troves.di

import com.troves.data.di.dataModule
import com.troves.domain.di.domainModule
import com.troves.presintation.di.presentationModule
import com.troves.presintation.di.presentationModule
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

/**
 * Initialises Koin for the whole app.
 * Call once — from the Android Application class or iOS entry point.
 */
fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    if (GlobalContext.getOrNull() != null) return
    startKoin {
        appDeclaration()
        modules(
            dataModule,
            domainModule,
            presentationModule,
            platformModule()
        )
    }
}