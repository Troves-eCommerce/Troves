package com.example.domain.di

import com.example.domain.GetProductsUseCase
import org.koin.dsl.module

val domainModule = module {
    // Use cases — factory creates a new instance per injection site
    factory { GetProductsUseCase(get()) }
}