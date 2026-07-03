package com.troves.data.di

import com.apollographql.apollo.ApolloClient
import com.troves.data.source.local.database.DatabaseFactory
import com.troves.data.source.local.database.TrovesDatabase
import com.troves.data.network.provideApolloClient
import com.troves.data.network.provideStorefrontApolloClient
import com.troves.data.network.provideHttpClient
import com.troves.data.network.provideLocationHttpClient
import com.troves.data.repository.AddressRepositoryImpl
import com.troves.data.repository.CartRepositoryImpl
import com.troves.data.repository.LocationRepositoryImpl
import com.troves.data.repository.OrderRepositoryImpl
import com.troves.data.repository.PaymentRepositoryImpl
import com.troves.data.repository.TrovesRepositoryImpl
import com.troves.data.repository.WishlistRepositoryImpl
import com.troves.data.repository.createAuthenticationRepository
import com.troves.data.source.local.preferenceses.TrovesPreferences
import com.troves.data.source.local.preferenceses.TrovesPreferencesImpl
import com.troves.data.source.remote.RemoteDatasource
import com.troves.data.source.remote.RemoteDatasourceImpl
import com.troves.data.source.remote.location.LocationApiService
import com.troves.data.source.remote.location.LocationApiServiceImpl
import com.troves.data.source.remote.location.LocationDataSource
import com.troves.data.source.remote.location.LocationDataSourceImpl
import com.troves.data.source.remote.service.TrovesApiService
import com.troves.data.source.remote.service.StorefrontApiService
import com.troves.data.source.remote.service.apollo.ApolloStorefrontApiServiceImpl
import com.troves.data.source.remote.service.apollo.ApolloTrovesApiServiceImpl
import com.troves.domain.repository.AddressRepository
import com.troves.domain.repository.AuthenticationRepository
import com.troves.domain.repository.CartRepository
import com.troves.domain.repository.LocationRepository
import com.troves.domain.repository.OrderRepository
import com.troves.domain.repository.PaymentRepository
import com.troves.domain.repository.TrovesRepository
import com.troves.domain.repository.WishlistRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.firestore
import io.ktor.client.HttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module

val dataModule = module {

    // ── Network ───────────────────────────────────────────────────────────────
    // Ktor client kept registered for easy rollback to the REST implementation.
    single<HttpClient> { provideHttpClient() }
    // Dedicated client for public location APIs — no Shopify auth/base URL leaks to third parties.
    single<HttpClient>(named(LOCATION_CLIENT)) { provideLocationHttpClient() }
    // Admin GraphQL client (product catalogue) and Storefront client (cart/checkout/customer/orders).
    single<ApolloClient>(named(ADMIN_CLIENT)) { provideApolloClient() }
    single<ApolloClient>(named(STORE_CLIENT)) { provideStorefrontApolloClient() }
    // GraphQL (Apollo) is now the active TrovesApiService implementation.
    single<TrovesApiService> { ApolloTrovesApiServiceImpl(get(named(ADMIN_CLIENT))) }
    single<StorefrontApiService> { ApolloStorefrontApiServiceImpl(get(named(STORE_CLIENT))) }

    // Location (countries/cities) — dedicated service → data source → repository.
    single<LocationApiService> { LocationApiServiceImpl(get(named(LOCATION_CLIENT))) }
    single<LocationDataSource> { LocationDataSourceImpl(get()) }

    // ── Remote data source ────────────────────────────────────────────────────
    single<RemoteDatasource> { RemoteDatasourceImpl(get(), get()) }

    // ── Local ─────────────────────────────────────────────────────────────────
    single<TrovesPreferences> { TrovesPreferencesImpl(get()) }

    // ── Database ──────────────────────────────────────────────────────────────
    single<TrovesDatabase> {
        get<DatabaseFactory>().createBuilder()
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }
    single { get<TrovesDatabase>().wishlistDao() }
    single { get<TrovesDatabase>().addressDao() }
    single { get<TrovesDatabase>().cartDao() }

    // ── Repositories ──────────────────────────────────────────────────────────
    single<TrovesRepository>          { TrovesRepositoryImpl(get(), get()) }
    single<AuthenticationRepository>  { createAuthenticationRepository(get(), get()) }
    single<PaymentRepository>         { PaymentRepositoryImpl() }
    single<CartRepository>            { CartRepositoryImpl(get(), get(), get(), get()) }
    single<OrderRepository>           { OrderRepositoryImpl(get(), get(), get(), get()) }
    single<WishlistRepository>        { WishlistRepositoryImpl(get() , get()) }
    single<LocationRepository>        { LocationRepositoryImpl(get()) }
    single<AddressRepository>         { AddressRepositoryImpl(get(), get(), get()) }
    single<FirebaseFirestore> { Firebase.firestore }
}

private const val ADMIN_CLIENT = "admin"
private const val STORE_CLIENT = "store"
private const val LOCATION_CLIENT = "location"
