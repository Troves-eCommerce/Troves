package com.troves.presintation.di

import com.troves.presintation.ui.MainViewModel
import com.troves.presintation.ui.products.ProductsViewModel
import com.troves.presintation.ui.home.HomeViewModel
import com.troves.presintation.ui.onboarding.OnboardingViewModel
import com.troves.presintation.ui.productDetails.ProductDetailsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val presentationModule = module {
    viewModelOf(::MainViewModel)
    viewModelOf(::OnboardingViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::ProductDetailsViewModel)
    viewModelOf(::ProductsViewModel)
}
