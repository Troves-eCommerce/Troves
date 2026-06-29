package com.troves.di

import com.troves.data.di.dataModule
import com.troves.domain.di.domainModule
import com.troves.presintation.di.presentationModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration


fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
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


fun doInitKoin() = initKoin()