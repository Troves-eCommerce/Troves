package com.troves.presintation.di

import com.troves.presintation.ui.MainViewModel
import com.troves.presintation.ui.auth.AuthViewModel
import com.troves.presintation.ui.fav.WishlistViewModel
import com.troves.presintation.ui.cart.CartViewModel
import com.troves.presintation.ui.products.ProductsViewModel
import com.troves.presintation.ui.home.HomeViewModel
import com.troves.presintation.ui.onboarding.OnboardingViewModel
import com.troves.presintation.ui.productDetails.ProductDetailsViewModel
import com.troves.presintation.ui.profile.ProfileViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val presentationModule = module {
    viewModelOf(::MainViewModel)
    viewModelOf(::AuthViewModel)
    viewModelOf(::OnboardingViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::ProductDetailsViewModel)
    viewModelOf(::ProductsViewModel)
    viewModelOf(::WishlistViewModel)
    viewModelOf(::CartViewModel)
    viewModelOf(::ProfileViewModel)
}
