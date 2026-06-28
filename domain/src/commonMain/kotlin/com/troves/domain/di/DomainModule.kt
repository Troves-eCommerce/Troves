package com.troves.domain.di

import com.troves.domain.GetProductsUseCase
import com.troves.domain.home.GetAdsUseCase
import com.troves.domain.home.GetBrandsUseCase
import com.troves.domain.home.GetCategoriesUseCase
import com.troves.domain.home.GetJustForYouProductsUseCase
import com.troves.domain.home.GetTrendingProductsUseCase
import org.koin.dsl.module

val domainModule = module {
    // Use cases — factory creates a new instance per injection site
    factory { GetProductsUseCase(get()) }

    // Home feed use cases
    factory { GetAdsUseCase(get()) }
    factory { GetBrandsUseCase(get()) }
    factory { GetCategoriesUseCase(get()) }
    factory { GetJustForYouProductsUseCase(get()) }
    factory { GetTrendingProductsUseCase(get()) }
}