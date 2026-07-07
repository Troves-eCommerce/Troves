package com.troves.data.di

import com.apollographql.apollo.ApolloClient
import com.troves.data.source.local.database.DatabaseFactory
import com.troves.data.source.local.database.TrovesDatabase
import com.troves.data.network.provideApolloClient
import com.troves.data.network.provideStorefrontApolloClient
import com.troves.data.network.provideHttpClient
import com.troves.data.network.provideLocationHttpClient
import com.troves.data.network.provideAiHttpClient
import com.troves.data.network.provideLocationClient
import com.troves.data.repository.AiAssistantRepositoryImpl
import com.troves.data.source.remote.ai.AiApiService
import com.troves.data.source.remote.ai.AiApiServiceImpl
import com.troves.data.source.remote.ai.AiDataSource
import com.troves.data.source.remote.ai.AiDataSourceImpl
import com.troves.domain.repository.AiAssistantRepository
import com.troves.data.network.providePaymobClient
import com.troves.data.repository.AddressRepositoryImpl
import com.troves.data.repository.CurrencyRepositoryImpl
import com.troves.data.repository.LocationRepositoryImpl
import com.troves.data.repository.PaymentRepositoryImpl
import com.troves.data.repository.TrovesRepositoryImpl
import com.troves.data.repository.WishlistRepositoryImpl
import com.troves.data.repository.createAuthenticationRepository
import com.troves.data.source.framework.location.datasource.LocationDatasource
import com.troves.data.source.framework.location.datasource.LocationDatasourceImpl
import com.troves.data.source.framework.location.service.LocationService
import com.troves.data.source.framework.location.service.provideLocationService
import com.troves.data.source.local.ads.LocalAdsDataSource
import com.troves.data.source.local.ads.LocalAdsDataSourceImpl
import com.troves.data.source.local.preferenceses.TrovesPreferences
import com.troves.data.source.local.preferenceses.TrovesPreferencesImpl
import com.troves.data.source.remote.CurrencyRemoteDataSource
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
import com.troves.data.source.remote.service.paymob.PaymobApiService
import com.troves.data.source.remote.service.paymob.PaymobServiceImpl
import com.troves.domain.repository.AddressRepository
import com.troves.domain.repository.AuthenticationRepository
import com.troves.domain.repository.CurrencyRepository
import com.troves.domain.repository.LocationRepository
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
    single<HttpClient>(named(PAYMOB)) { providePaymobClient() }
    single<HttpClient>(named(LOCATION_IQ)) { provideLocationClient() }

    single<HttpClient>(named(LOCATION_CLIENT)) { provideLocationHttpClient() }


    // Admin GraphQL client (product catalogue) and Storefront client (cart/checkout/customer/orders).
    single<ApolloClient>(named(ADMIN_CLIENT)) { provideApolloClient() }
    single<ApolloClient>(named(STORE_CLIENT)) { provideStorefrontApolloClient() }


    // GraphQL (Apollo) is now the active TrovesApiService implementation.
    single<TrovesApiService> { ApolloTrovesApiServiceImpl(get(named(ADMIN_CLIENT))) }
    single<StorefrontApiService> { ApolloStorefrontApiServiceImpl(get(named(STORE_CLIENT))) }



    single<PaymobApiService> { PaymobServiceImpl(get(named(PAYMOB))) }



    // Location (countries/cities) — dedicated service → data source → repository.
    single<LocationApiService> { LocationApiServiceImpl(get(named(LOCATION_CLIENT))) }
    single<LocationDataSource> { LocationDataSourceImpl(get()) }



    // AI assistant — dedicated client (no Shopify auth) → service (swap seam) → data source.
    single<HttpClient>(named(AI_CLIENT)) { provideAiHttpClient() }
    single<AiApiService> { AiApiServiceImpl(get(named(AI_CLIENT))) }
    single<AiDataSource> { AiDataSourceImpl(get()) }

    // ── Remote data source ────────────────────────────────────────────────────
    single<RemoteDatasource> { RemoteDatasourceImpl(get(), get()) }
    single { CurrencyRemoteDataSource(get(named(LOCATION_CLIENT))) }

    // ── Local ─────────────────────────────────────────────────────────────────
    single<TrovesPreferences> { TrovesPreferencesImpl(get()) }
    single<LocalAdsDataSource> { LocalAdsDataSourceImpl() }

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
    single<TrovesRepository>          { TrovesRepositoryImpl(get(), get(), get(), get(), get(), get()) }
    single<AuthenticationRepository>  { createAuthenticationRepository(get(), get(), get()) }
    single<PaymentRepository>         { PaymentRepositoryImpl(get()) }
    single<WishlistRepository>        { WishlistRepositoryImpl(get() , get()) }
    single<LocationRepository>        { LocationRepositoryImpl(get()) }
    single<AddressRepository>         { AddressRepositoryImpl(get(), get(), get()) }
    single { AiAssistantRepositoryImpl(get(), get(), get()) }
    single<AiAssistantRepository>     { get<AiAssistantRepositoryImpl>() }
    single<CurrencyRepository>        { CurrencyRepositoryImpl(get(), get()) }
    single<FirebaseFirestore> { Firebase.firestore }





    //LocationService
    single<LocationService> { provideLocationService() }
    single<LocationDatasource> { LocationDatasourceImpl(get(),get(named(LOCATION_IQ))) }







}

private const val ADMIN_CLIENT = "admin"
private const val STORE_CLIENT = "store"
private const val LOCATION_CLIENT = "location"
private const val PAYMOB = "paymob"
private const val LOCATION_IQ = "location_iq"
private const val AI_CLIENT = "ai"