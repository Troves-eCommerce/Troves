package com.troves.presintation.di

import com.troves.presintation.products.ProductsViewModel
import com.troves.presintation.ui.auth.AuthViewModel
import com.troves.presintation.ui.onboarding.OnboardingViewModel
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    // ViewModels — koin-compose-viewmodel handles lifecycle automatically
    viewModel { ProductsViewModel(get()) }

    viewModel { OnboardingViewModel() }
    viewModel { AuthViewModel(get(), get()) }
}