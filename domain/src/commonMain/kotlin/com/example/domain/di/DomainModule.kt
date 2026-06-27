package com.example.domain.di

import org.koin.dsl.module

/**
 * Domain-layer Koin module.
 * Use cases and domain services are registered here as the project grows.
 */
val domainModule = module {
    // Use cases will be registered here, e.g.:
    // factory { GetProductsUseCase(get()) }
}