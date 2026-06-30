package com.troves.domain.di

import com.troves.domain.usecase.auth.IsLoggedInUseCase
import com.troves.domain.usecase.auth.LoginUseCase
import com.troves.domain.usecase.auth.LogoutUseCase
import com.troves.domain.usecase.auth.RegisterUseCase
import com.troves.domain.usecase.auth.SignInWithGoogleUseCase
import com.troves.domain.usecase.cart.AddToCartUseCase
import com.troves.domain.usecase.cart.GetCartStreamUseCase
import com.troves.domain.usecase.cart.RemoveFromCartUseCase
import com.troves.domain.usecase.cart.UpdateCartQuantityUseCase
import com.troves.domain.usecase.details.GetProductByIdUseCase
import com.troves.domain.usecase.home.GetAdsUseCase
import com.troves.domain.usecase.home.GetBrandsUseCase
import com.troves.domain.usecase.home.GetCategoriesUseCase
import com.troves.domain.usecase.home.GetJustForYouProductsUseCase
import com.troves.domain.usecase.home.GetTrendingProductsUseCase
import com.troves.domain.usecase.onboarding.CompleteOnboardingUseCase
import com.troves.domain.usecase.onboarding.IsOnboardingDoneUseCase
import com.troves.domain.usecase.products.FilterProductsUseCase
import com.troves.domain.usecase.products.SortProductsUseCase
import com.troves.domain.usecase.shared.GetProductsUseCase
import org.koin.dsl.module

val domainModule = module {
    // Use cases — factory creates a new instance per injection site
    factory { GetProductsUseCase(get()) }
    factory { GetProductByIdUseCase(get()) }

    // Products listing — filter & sort
    factory { FilterProductsUseCase() }
    factory { SortProductsUseCase() }

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
    factory { SignInWithGoogleUseCase(get()) }
    factory { IsLoggedInUseCase(get()) }
    factory { LogoutUseCase(get()) }

    factory { GetCartStreamUseCase(get()) }
    factory { AddToCartUseCase(get()) }
    factory { RemoveFromCartUseCase(get()) }
    factory { UpdateCartQuantityUseCase(get()) }
}
