package com.troves.domain.di

import com.troves.domain.GetProductsUseCase
import org.koin.dsl.module

val domainModule = module {
    // Use cases — factory creates a new instance per injection site
    factory { GetProductsUseCase(get()) }
}