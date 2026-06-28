package com.troves.di

import com.troves.data.local.preferenceses.createDataStoreIos
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module { single { createDataStoreIos() } }
