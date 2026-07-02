package com.troves.data.di

import com.apollographql.apollo.ApolloClient
import com.troves.data.source.local.database.DatabaseFactory
import com.troves.data.source.local.database.TrovesDatabase
import com.troves.data.network.provideApolloClient
import com.troves.data.network.provideHttpClient
import com.troves.data.repository.CartRepositoryImpl
import com.troves.data.repository.PaymentRepositoryImpl
import com.troves.data.repository.TrovesRepositoryImpl
import com.troves.data.repository.WishlistRepositoryImpl
import com.troves.data.repository.createAuthenticationRepository
import com.troves.data.source.local.preferenceses.TrovesPreferences
import com.troves.data.source.local.preferenceses.TrovesPreferencesImpl
import com.troves.data.source.remote.RemoteDatasource
import com.troves.data.source.remote.RemoteDatasourceImpl
import com.troves.data.source.remote.service.TrovesApiService
import com.troves.data.source.remote.service.apollo.ApolloTrovesApiServiceImpl
import com.troves.domain.repository.AuthenticationRepository
import com.troves.domain.repository.CartRepository
import com.troves.domain.repository.PaymentRepository
import com.troves.domain.repository.TrovesRepository
import com.troves.domain.repository.WishlistRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.firestore
import io.ktor.client.HttpClient
import org.koin.dsl.module

val dataModule = module {

    // ── Network ───────────────────────────────────────────────────────────────
    // Ktor client kept registered for easy rollback to the REST implementation.
    single<HttpClient> { provideHttpClient() }
    single<ApolloClient> {
        provideApolloClient()
    }
    // GraphQL (Apollo) is now the active TrovesApiService implementation.
    single<TrovesApiService> { ApolloTrovesApiServiceImpl(get()) }

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
    single { get<TrovesDatabase>().cartDao() }

    // ── Repositories ──────────────────────────────────────────────────────────
    single<TrovesRepository>          { TrovesRepositoryImpl(get()) }
    single<AuthenticationRepository>  { createAuthenticationRepository(get()) }
    single<PaymentRepository>         { PaymentRepositoryImpl() }
    single<CartRepository>            { CartRepositoryImpl(get(), get()) }
    single<WishlistRepository>        { WishlistRepositoryImpl(get() , get()) }
    single<FirebaseFirestore> { Firebase.firestore }
}
