package com.troves.domain.di

import com.troves.domain.usecase.auth.IsLoggedInUseCase
import com.troves.domain.usecase.auth.LoginUseCase
import com.troves.domain.usecase.auth.RegisterUseCase
import com.troves.domain.usecase.details.GetProductByIdUseCase
import com.troves.domain.usecase.home.GetAdsUseCase
import com.troves.domain.usecase.home.GetBrandsUseCase
import com.troves.domain.usecase.home.GetCategoriesUseCase
import com.troves.domain.usecase.home.GetJustForYouProductsUseCase
import com.troves.domain.usecase.home.GetTrendingProductsUseCase
import com.troves.domain.usecase.onboarding.CompleteOnboardingUseCase
import com.troves.domain.usecase.onboarding.IsOnboardingDoneUseCase
import com.troves.domain.usecase.shared.GetProductsUseCase
import org.koin.dsl.module

val domainModule = module {
    // Use cases — factory creates a new instance per injection site
    factory { GetProductsUseCase(get()) }
    factory { GetProductByIdUseCase(get()) }

    // Home feed use cases
    factory { GetAdsUseCase(get()) }
    factory { GetBrandsUseCase(get()) }
    factory { GetCategoriesUseCase(get()) }
    factory { GetJustForYouProductsUseCase(get()) }
    factory { GetTrendingProductsUseCase(get()) }

    // Onboarding
    factory { IsOnboardingDoneUseCase(get()) }
    factory { CompleteOnboardingUseCase(get()) }

    // Authentication
    factory { LoginUseCase(get()) }
    factory { RegisterUseCase(get()) }
    factory { IsLoggedInUseCase(get()) }
}
