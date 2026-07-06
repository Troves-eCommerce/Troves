package com.troves.domain.di

import com.troves.domain.usecase.ai.DeleteAiConversationUseCase
import com.troves.domain.usecase.ai.GetAiConversationsUseCase
import com.troves.domain.usecase.ai.SaveAiConversationUseCase
import com.troves.domain.usecase.ai.SendAiMessageUseCase
import com.troves.domain.usecase.auth.IsLoggedInUseCase
import com.troves.domain.usecase.auth.LoginUseCase
import com.troves.domain.usecase.auth.LogoutUseCase
import com.troves.domain.usecase.auth.RegisterUseCase
import com.troves.domain.usecase.auth.SignInWithGoogleUseCase
import com.troves.domain.usecase.cart.AddToCartUseCase
import com.troves.domain.usecase.cart.ApplyDiscountUseCase
import com.troves.domain.usecase.cart.GetCartStreamUseCase
import com.troves.domain.usecase.cart.RefreshCartUseCase
import com.troves.domain.usecase.cart.RemoveAllFromCartUseCase
import com.troves.domain.usecase.cart.RemoveFromCartUseCase
import com.troves.domain.usecase.cart.SyncCartUseCase
import com.troves.domain.usecase.cart.UpdateCartQuantityUseCase
import com.troves.domain.usecase.details.GetProductByIdUseCase
import com.troves.domain.usecase.home.GetAdsUseCase
import com.troves.domain.usecase.home.GetBrandsUseCase
import com.troves.domain.usecase.home.GetCategoriesUseCase
import com.troves.domain.usecase.home.GetDiscountCodesUseCase
import com.troves.domain.usecase.home.GetJustForYouProductsUseCase
import com.troves.domain.usecase.home.GetTrendingProductsUseCase
import com.troves.domain.usecase.onboarding.CompleteOnboardingUseCase
import com.troves.domain.usecase.onboarding.IsOnboardingDoneUseCase
import com.troves.domain.usecase.order.ClearCartUseCase
import com.troves.domain.usecase.order.GetDefaultAddressUseCase
import com.troves.domain.usecase.order.GetOrderByIdUseCase
import com.troves.domain.usecase.order.GetOrdersUseCase
import com.troves.domain.usecase.order.PlaceCodOrderUseCase
import com.troves.domain.usecase.paymob.GetClientSecretUseCase
import com.troves.domain.usecase.products.FilterProductsUseCase
import com.troves.domain.usecase.products.GetProductsByBrandUseCase
import com.troves.domain.usecase.products.GetProductsByCategoryUseCase
import com.troves.domain.usecase.products.SortProductsUseCase
import com.troves.domain.usecase.search.FilterProductsByQueryUseCase
import com.troves.domain.usecase.search.SearchProductsUseCase
import com.troves.domain.usecase.settings.FetchLatestRatesUseCase
import com.troves.domain.usecase.settings.GetExchangeRatesUseCase
import com.troves.domain.usecase.settings.ObserveProfilePreferencesUseCase
import com.troves.domain.usecase.settings.SetCurrencyUseCase
import com.troves.domain.usecase.settings.SetLanguageUseCase
import com.troves.domain.usecase.settings.SetThemeModeUseCase
import com.troves.domain.usecase.shared.GetProductsUseCase
import com.troves.domain.usecase.wishlist.GetWishlistUseCase
import com.troves.domain.usecase.wishlist.IsProductFavoritedUseCase
import com.troves.domain.usecase.wishlist.SyncWishlistUseCase
import com.troves.domain.usecase.wishlist.ToggleFavoriteUseCase
import org.koin.dsl.module

val domainModule = module {
    // Use cases — factory creates a new instance per injection site
    factory { GetProductsUseCase(get()) }
    factory { GetProductByIdUseCase(get()) }

    // Products listing — filter, sort & source-scoped fetch
    factory { FilterProductsUseCase() }
    factory { SortProductsUseCase() }
    factory { GetProductsByBrandUseCase(get()) }
    factory { GetProductsByCategoryUseCase(get()) }

    // Home feed use cases
    factory { GetAdsUseCase(get()) }
    factory { GetBrandsUseCase(get()) }
    factory { GetCategoriesUseCase(get()) }
    factory { GetJustForYouProductsUseCase(get()) }
    factory { GetTrendingProductsUseCase(get()) }
    factory { FilterProductsByQueryUseCase(get()) }
    factory { SearchProductsUseCase(get()) }
    factory { GetDiscountCodesUseCase(get()) }

    // Onboarding
    factory { IsOnboardingDoneUseCase(get()) }
    factory { CompleteOnboardingUseCase(get()) }

    // Survey
    factory { com.troves.domain.usecase.survey.IsSurveyDoneUseCase(get()) }
    factory { com.troves.domain.usecase.survey.CompleteSurveyUseCase(get()) }
    factory { com.troves.domain.usecase.survey.ObserveSurveyDoneUseCase(get()) }


    // Wishlist
    factory { GetWishlistUseCase(get()) }
    factory { IsProductFavoritedUseCase(get()) }
    factory { ToggleFavoriteUseCase(get(),get()) }

    // Authentication
    factory { LoginUseCase(get()) }
    factory { RegisterUseCase(get()) }
    factory { SignInWithGoogleUseCase(get()) }
    factory { IsLoggedInUseCase(get()) }
    factory { LogoutUseCase(get(), get(), get()) }

    factory { GetCartStreamUseCase(get()) }
    factory { AddToCartUseCase(get(), get()) }
    factory { RemoveFromCartUseCase(get(), get()) }
    factory { RemoveAllFromCartUseCase(get(), get()) }
    factory { UpdateCartQuantityUseCase(get(), get()) }
    factory { ApplyDiscountUseCase(get(), get()) }
    factory { RefreshCartUseCase(get(), get()) }
    single { SyncCartUseCase(get(), get()) }

    // Checkout / orders
    factory { GetDefaultAddressUseCase(get()) }
    factory { GetOrdersUseCase(get()) }
    factory { GetOrderByIdUseCase(get()) }
    factory { PlaceCodOrderUseCase(get(), get()) }
    factory { com.troves.domain.usecase.order.AttachAddressToCartUseCase(get()) }
    factory { ClearCartUseCase(get()) }
    factory { GetClientSecretUseCase(get()) }
    single { SyncWishlistUseCase(get(),get()) }

    // Address
    factory { com.troves.domain.usecase.address.GetSavedAddressesUseCase(get()) }
    factory { com.troves.domain.usecase.address.AddAddressUseCase(get()) }
    factory { com.troves.domain.usecase.address.UpdateAddressUseCase(get()) }
    factory { com.troves.domain.usecase.address.DeleteAddressUseCase(get()) }
    factory { com.troves.domain.usecase.address.SetDefaultAddressUseCase(get()) }
    factory { com.troves.domain.usecase.address.GetSavedAddressByIdUseCase(get()) }
    factory { com.troves.domain.usecase.address.GetDefaultSavedAddressUseCase(get()) }
    factory { com.troves.domain.usecase.address.RefreshAddressesUseCase(get()) }
    factory { com.troves.domain.usecase.shared.GetCountriesUseCase(get()) }
    factory { com.troves.domain.usecase.shared.GetCitiesUseCase(get()) }

    // Settings
    factory { SetLanguageUseCase(get()) }
    factory { SetCurrencyUseCase(get()) }
    factory { SetThemeModeUseCase(get()) }
    factory { ObserveProfilePreferencesUseCase(get(), get()) }
    factory { GetExchangeRatesUseCase(get()) }
    factory { FetchLatestRatesUseCase(get()) }

    // AI assistant
    factory { SendAiMessageUseCase(get()) }
    factory { GetAiConversationsUseCase(get(), get()) }
    factory { SaveAiConversationUseCase(get(), get()) }
    factory { DeleteAiConversationUseCase(get(), get()) }
}
