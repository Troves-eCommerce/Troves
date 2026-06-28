package com.troves.di

import com.troves.TrovesApp.Companion.trovesApplication
import com.troves.data.local.preferenceses.createDataStoreAndroid
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    module { single { createDataStoreAndroid(trovesApplication) } }
}
