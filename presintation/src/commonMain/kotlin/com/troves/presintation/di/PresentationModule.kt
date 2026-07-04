package com.troves.presintation.di

//import com.troves.presintation.ui.AppViewModel
import com.troves.presintation.ui.MainViewModel
import com.troves.presintation.ui.allbrands.AllBrandsViewModel
import com.troves.presintation.ui.auth.AuthViewModel
import com.troves.presintation.ui.fav.WishlistViewModel
import com.troves.presintation.ui.cart.CartViewModel
import com.troves.presintation.ui.checkout.CheckoutViewModel
import com.troves.presintation.ui.orders.OrdersViewModel
import com.troves.presintation.ui.products.ProductsViewModel
import com.troves.presintation.ui.home.HomeViewModel
import com.troves.presintation.ui.onboarding.OnboardingViewModel
import com.troves.presintation.ui.productDetails.ProductDetailsViewModel
import com.troves.presintation.ui.search.SearchScreenViewModel
import com.troves.presintation.ui.profile.ProfileViewModel
import com.troves.presintation.ui.orderdetails.OrderDetailsViewModel
import com.troves.presintation.ui.address.ManageSavedAddressesViewModel
import com.troves.presintation.ui.address.NewAddressViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val presentationModule = module {
    //viewModelOf(::AppViewModel)
    viewModelOf(::MainViewModel)
    viewModelOf(::AuthViewModel)
    viewModelOf(::OnboardingViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::ProductDetailsViewModel)
    viewModelOf(::ProductsViewModel)
    viewModelOf(::SearchScreenViewModel)
    viewModelOf(::WishlistViewModel)
    viewModelOf(::AllBrandsViewModel)
    viewModelOf(::CartViewModel)
    viewModelOf(::CheckoutViewModel)
    viewModelOf(::OrdersViewModel)
    viewModelOf(::OrderDetailsViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::ManageSavedAddressesViewModel)
    viewModelOf(::NewAddressViewModel)
}
