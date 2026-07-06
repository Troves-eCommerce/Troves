package com.troves.presintation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.troves.designsystem.components.bottomnav.BottomNavItem
import com.troves.designsystem.components.bottomnav.SPBottomNavigation
import com.troves.presintation.ui.MainViewModel
import com.troves.presintation.ui.StartDestination
import com.troves.presintation.ui.address.ManageSavedAddressesScreen
import com.troves.presintation.ui.address.NewAddressScreen
import com.troves.presintation.ui.aichat.AiChatScreen
import com.troves.presintation.ui.aichat.AiChatViewModel
import com.troves.presintation.ui.auth.LoginScreen
import com.troves.presintation.ui.auth.RegisterScreen
import com.troves.presintation.ui.cart.CartScreen
import com.troves.presintation.ui.checkout.CheckoutScreen
import com.troves.presintation.ui.fav.WishlistScreen
import com.troves.presintation.ui.home.HomeScreen
import com.troves.presintation.ui.onboarding.OnboardingScreen
import com.troves.presintation.ui.orderresult.OrderResultScreen
import com.troves.presintation.ui.orders.OrdersScreen
import com.troves.presintation.ui.payment.PaymentMethodsScreen
import com.troves.presintation.ui.productDetails.ProductDetailsScreen
import com.troves.presintation.ui.products.ProductsScreen
import com.troves.presintation.ui.profile.ProfileScreen
import com.troves.presintation.ui.search.SearchScreen
import com.troves.presintation.ui.search.SearchScreenViewModel
import com.troves.presintation.ui.seeall.SeeAllScreen
import com.troves.presintation.ui.splash.SplashScreen
import com.troves.presintation.ui.survey.SurveyScreen
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.koin.compose.viewmodel.koinViewModel
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.ic_home
import troves.designsystem.generated.resources.ic_order
import troves.designsystem.generated.resources.ic_profile
import troves.designsystem.generated.resources.ic_wishlist

private val navSavedStateConfiguration = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(AppRoute.Splash::class, AppRoute.Splash.serializer())
            subclass(AppRoute.Onboarding::class, AppRoute.Onboarding.serializer())
            subclass(AppRoute.Login::class, AppRoute.Login.serializer())
            subclass(AppRoute.Register::class, AppRoute.Register.serializer())
            subclass(AppRoute.Home::class, AppRoute.Home.serializer())
            subclass(AppRoute.Products::class, AppRoute.Products.serializer())
            subclass(AppRoute.SeeAll::class, AppRoute.SeeAll.serializer())
            subclass(AppRoute.Favorites::class, AppRoute.Favorites.serializer())
            subclass(AppRoute.Profile::class, AppRoute.Profile.serializer())
            subclass(AppRoute.AiChat::class, AppRoute.AiChat.serializer())
            subclass(AppRoute.ProductDetails::class, AppRoute.ProductDetails.serializer())
            subclass(AppRoute.Cart::class, AppRoute.Cart.serializer())
            subclass(AppRoute.Checkout::class, AppRoute.Checkout.serializer())
            subclass(AppRoute.Orders::class, AppRoute.Orders.serializer())
            subclass(AppRoute.Search::class, AppRoute.Search.serializer())
            subclass(AppRoute.ManageAddresses::class, AppRoute.ManageAddresses.serializer())
            subclass(AppRoute.NewAddress::class, AppRoute.NewAddress.serializer())
            subclass(AppRoute.PaymentMethods::class, AppRoute.PaymentMethods.serializer())
            subclass(AppRoute.OrderResult::class, AppRoute.OrderResult.serializer())
            subclass(AppRoute.Survey::class, AppRoute.Survey.serializer())
        }
    }
}

@Composable
fun AppNav() {
    val mainViewModel: MainViewModel = koinViewModel()
    val uiState by mainViewModel.uiState.collectAsState()

    val initialRoute: NavKey = AppRoute.Splash

    val backStack = rememberNavBackStack(navSavedStateConfiguration, initialRoute)
    val currentRoute = backStack.lastOrNull()

    val bottomNavRoutes = remember {
        listOf(
            AppRoute.Home,
            AppRoute.Favorites,
            AppRoute.Orders,
            AppRoute.Profile
        )
    }

    val selectedIndex = bottomNavRoutes.indexOf(currentRoute)
    val shouldShowBottomBar = currentRoute in bottomNavRoutes && currentRoute != AppRoute.Products

    fun replaceWith(route: NavKey) {
        Snapshot.withMutableSnapshot {
            backStack.clear()
            backStack.add(route)
        }
    }

    fun onBottomNavItemSelected(index: Int) {
        val targetRoute = bottomNavRoutes[index]
        if (currentRoute != targetRoute) {
            Snapshot.withMutableSnapshot {
                backStack.clear()
                backStack.add(targetRoute)
            }
        }
    }

    val entryProvider: (NavKey) -> NavEntry<NavKey> = entryProvider {
        entry<AppRoute.Home> {
            HomeScreen(
                onNavigateToProduct = { productId ->
                    backStack.add(AppRoute.ProductDetails(productId))
                },
                onNavigateToRegister = { backStack.add(AppRoute.Register) },
                onNavigateToSearch = {backStack.add(AppRoute.Search)},
                onNavigateToCart = { backStack.add(AppRoute.Cart) },
                onNavigateToAllCategories = {
                    backStack.add(AppRoute.SeeAll(AppRoute.SeeAllType.CATEGORIES, name = "Categories"))
                },
                onNavigateToAllBrands = {
                    backStack.add(AppRoute.SeeAll(AppRoute.SeeAllType.BRANDS, name = "Brands"))
                },
                onNavigateToProducts = { sourceType, sourceId, sourceName ->
                    backStack.add(
                        AppRoute.SeeAll(
                            type = AppRoute.SeeAllType.PRODUCTS,
                            id = sourceId,
                            name = sourceName,
                        ),
                    )
                },
                onNavigateToSurvey = { backStack.add(AppRoute.Survey) },
                onNavigateToAiChat = {
                    backStack.add(
                        AppRoute.AiChat
                    )
                }
            )
        }
        entry<AppRoute.Favorites> {
            WishlistScreen(
                onNavigateToProduct = { productId ->
                    backStack.add(AppRoute.ProductDetails(productId))
                },
                onNavigateToRegister = { backStack.add(AppRoute.Register) },
            )
        }
        entry<AppRoute.ProductDetails> { key ->
            ProductDetailsScreen(
                productId = key.productId,
                onNavigateBack = { backStack.removeLastOrNull() },
                onNavigateToLogin = { backStack.add(AppRoute.Login) },
                onNavigateToCart = { backStack.add(AppRoute.Cart) },
            )
        }
        entry<AppRoute.Onboarding> {
            OnboardingScreen(
                onOnboardingComplete = { replaceWith(AppRoute.Home) }
            )
        }
        entry<AppRoute.Splash> {
            var splashFinished by remember { mutableStateOf(false) }

            val destination = remember(uiState.isLoading, uiState.startDestination) {
                if (uiState.isLoading) null
                else when (uiState.startDestination) {
                    StartDestination.Onboarding -> AppRoute.Onboarding
                    StartDestination.Home -> AppRoute.Home
                }
            }

            SplashScreen(
                onNavigateToOnboarding = {
                    splashFinished = true
                }
            )

            LaunchedEffect(splashFinished, destination) {
                if (splashFinished && destination != null) {
                    replaceWith(destination)
                }
            }
        }
        entry<AppRoute.Login> {
            LoginScreen(
                onNavigateToRegister = { backStack.add(AppRoute.Register) },
                onLoginSuccess = { replaceWith(AppRoute.Home) },
                onLoggedIn = {
                }
            )
        }
        entry<AppRoute.Register> {
            RegisterScreen(
                onNavigateToLogin = { backStack.add(AppRoute.Login) },
                onRegisterSuccess = { replaceWith(AppRoute.Home) },
                onRegistered = {
                }
            )
        }
        entry<AppRoute.SeeAll> { key ->
            SeeAllScreen(
                type = key.type,
                id = key.id,
                name = key.name,
                onNavigateBack = { backStack.removeLastOrNull() },
                onNavigateToProducts = { sourceType, sourceId, sourceName ->
                    backStack.add(AppRoute.SeeAll(AppRoute.SeeAllType.PRODUCTS, sourceId, sourceName))
                },
                onNavigateToProductDetails = { productId ->
                    backStack.add(AppRoute.ProductDetails(productId))
                }
            )
        }
        entry<AppRoute.Products> { key ->
            ProductsScreen(
                sourceType = key.sourceType,
                sourceId = key.sourceId,
                sourceName = key.sourceName,
                onNavigateToProduct = { productId ->
                    backStack.add(AppRoute.ProductDetails(productId))
                },
                onNavigateBack = { backStack.removeLastOrNull() },
            )
        }
        entry<AppRoute.Profile> {
            ProfileScreen(
                onNavigateToLogin = { replaceWith(AppRoute.Login) },
                onNavigateToAddresses = { backStack.add(AppRoute.ManageAddresses) },
                onNavigateToOrders = { backStack.add(AppRoute.Orders) },
                onNavigateToPaymentMethods = { backStack.add(AppRoute.PaymentMethods) },
                onNavigateToEditProfile = { /* Navigate to Edit Profile screen if it exists */ },
                onNavigateToAiAssistant = { backStack.add(AppRoute.AiChat) },
                onNavigateToSurvey = { backStack.add(AppRoute.Survey) },
            )
        }
        entry<AppRoute.AiChat> {
            val viewModel: AiChatViewModel = koinViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()
            AiChatScreen(
                state = state,
                effect = viewModel.effect,
                onIntent = viewModel::onIntent,
                onNavigateBack = { backStack.removeLastOrNull() },
                onNavigateToProduct = { productId -> backStack.add(AppRoute.ProductDetails(productId)) },
                onNavigateToSearch = { backStack.add(AppRoute.Search) },
            )
        }
        entry<AppRoute.PaymentMethods> {
            PaymentMethodsScreen()
        }
        entry<AppRoute.Cart> {
            CartScreen(
                onNavigateBack = { backStack.removeLastOrNull() },
                onNavigateToCheckout = { backStack.add(AppRoute.Checkout) },
                onNavigateToLogin = { backStack.add(AppRoute.Login) },
            )
        }
        entry<AppRoute.Checkout> {
            CheckoutScreen(
                onNavigateBack = { backStack.removeLastOrNull() },
                onNavigateToCart = { backStack.removeLastOrNull() },
                onNavigateToLogin = { backStack.add(AppRoute.Login) },
                onNavigateToNewAddress = { addressId -> backStack.add(AppRoute.NewAddress(addressId)) },
                onNavigateToOrderResult = { args ->
                    Snapshot.withMutableSnapshot {
                        backStack.clear()
                        backStack.add(AppRoute.Home)
                        backStack.add(args)
                    }
                },
            )
        }
        entry<AppRoute.OrderResult> { key ->
            OrderResultScreen(
                args = key,
                onGoHome = { replaceWith(AppRoute.Home) },
            )
        }
        entry<AppRoute.Orders> {
            OrdersScreen()
        }
        entry<AppRoute.Search> {
            val viewModel: SearchScreenViewModel = koinViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()
            val onIntent = viewModel::onIntent
            SearchScreen(
                state = state,
                onIntent = onIntent,
                effect = viewModel.effect,
                onNavigateToDetails = {
                    backStack.add(AppRoute.ProductDetails(it))
                },
                onNavigateBack = {
                    backStack.removeLastOrNull()
                }
            )
        }
        entry<AppRoute.ManageAddresses> {
            ManageSavedAddressesScreen(
                onNavigateToNewAddress = { backStack.add(AppRoute.NewAddress()) },
                onNavigateToEditAddress = { address ->
                    backStack.add(AppRoute.NewAddress(addressId = address.id))
                },
                onNavigateToLogin = { backStack.add(AppRoute.Login) },
                onNavigateBack = { backStack.removeLastOrNull() }
            )
        }
        entry<AppRoute.NewAddress> { key ->
            NewAddressScreen(
                onNavigateBack = { backStack.removeLastOrNull() },
                onNavigateToLogin = { backStack.add(AppRoute.Login) },
                addressId = key.addressId,
            )
        }
        entry<AppRoute.Survey> {
            SurveyScreen(
                onNavigateBack = { backStack.removeLastOrNull() },
            )
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (shouldShowBottomBar) {
                SPBottomNavigation(
                    items = listOf(
                        BottomNavItem("Home", Res.drawable.ic_home),
                        BottomNavItem("Wishlist", Res.drawable.ic_wishlist),
                        BottomNavItem("Orders", Res.drawable.ic_order),
                        BottomNavItem("Profile", Res.drawable.ic_profile)
                    ),
                    selectedIndex = if (selectedIndex != -1) selectedIndex else 0,
                    onItemSelected = { index -> onBottomNavItemSelected(index) }
                )
            }
        }
    ) { paddingValues ->
        NavDisplay<NavKey>(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
            ,
            entries = rememberDecoratedNavEntries(
                backStack = backStack,
                entryProvider = entryProvider
            ),
            onBack = { if (backStack.size > 1) backStack.removeLastOrNull() }
        )
    }
}