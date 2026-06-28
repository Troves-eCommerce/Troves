package com.troves.domain.di

import com.troves.domain.GetProductsUseCase
import com.troves.domain.LoginUseCase
import com.troves.domain.LogoutUseCase
import com.troves.domain.RegisterUseCase
import org.koin.dsl.module

val domainModule = module {
    // Use cases — factory creates a new instance per injection site
    factory { GetProductsUseCase(get()) }
    factory { LoginUseCase(get()) }
    factory { RegisterUseCase(get()) }
    factory { LogoutUseCase(get()) }
}