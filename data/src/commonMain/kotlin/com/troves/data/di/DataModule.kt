package com.troves.data.di

import com.troves.data.local.database.DatabaseFactory
import com.troves.data.local.database.TrovesDatabase
import com.troves.data.local.preferenceses.AppPreferencesDataSource
import com.troves.data.local.preferenceses.AppPreferencesDataSourceImpl
import com.troves.data.network.provideHttpClient
import com.troves.data.repository.PaymentRepositoryImpl
import com.troves.data.repository.TrovesRepositoryImpl
import com.troves.data.repository.WishlistRepositoryImpl
import com.troves.data.repository.createAuthenticationRepository
import com.troves.data.source.remote.RemoteDatasource
import com.troves.data.source.remote.RemoteDatasourceImpl
import com.troves.data.source.remote.service.TrovesApiService
import com.troves.data.source.remote.service.TrovesApiServiceImpl
import com.troves.domain.AuthenticationRepository
import com.troves.domain.repository.PaymentRepository
import com.troves.domain.repository.TrovesRepository
import com.troves.domain.repository.WishlistRepository
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import io.ktor.client.HttpClient
import org.koin.dsl.module

val dataModule = module {

    // ── Network ───────────────────────────────────────────────────────────────
    single<HttpClient> { provideHttpClient() }
    single<TrovesApiService> { TrovesApiServiceImpl(get()) }

    // ── Remote data source ────────────────────────────────────────────────────
    single<RemoteDatasource> { RemoteDatasourceImpl(get()) }

    // ── Local ─────────────────────────────────────────────────────────────────
    single<AppPreferencesDataSource> { AppPreferencesDataSourceImpl(get()) }

    // ── Database ──────────────────────────────────────────────────────────────
    single<TrovesDatabase> {
        get<DatabaseFactory>().createBuilder()
            .setDriver(BundledSQLiteDriver())
            .build()
    }
    single { get<TrovesDatabase>().wishlistDao() }

    // ── Repositories ──────────────────────────────────────────────────────────
    single<TrovesRepository>          { TrovesRepositoryImpl(get()) }
    single<AuthenticationRepository>  { createAuthenticationRepository(get()) }
    single<PaymentRepository>         { PaymentRepositoryImpl() }
    single<WishlistRepository>        { WishlistRepositoryImpl(get()) }
}
