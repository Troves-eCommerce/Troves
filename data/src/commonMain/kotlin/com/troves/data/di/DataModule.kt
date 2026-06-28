package com.troves.data.di

import com.troves.data.network.provideHttpClient
import com.troves.data.repository.AuthenticationRepositoryImpl
import com.troves.data.repository.FakeHomeRepository
import com.troves.data.repository.PaymentRepositoryImpl
import com.troves.data.repository.TrovesRepositoryImpl
import com.troves.data.source.remote.RemoteDatasource
import com.troves.data.source.remote.RemoteDatasourceImpl
import com.troves.data.source.remote.service.TrovesApiService
import com.troves.data.source.remote.service.TrovesApiServiceImpl
import com.troves.domain.AuthenticationRepository
import com.troves.domain.PaymentRepository
import com.troves.domain.TrovesRepository
import com.troves.domain.home.HomeRepository
import io.ktor.client.HttpClient
import org.koin.dsl.module

val dataModule = module {

    // ── Network ───────────────────────────────────────────────────────────────
    // single<T> means one shared instance for the entire app lifetime
    single<HttpClient> { provideHttpClient() }
    single<TrovesApiService> { TrovesApiServiceImpl(get()) }

    // ── Remote data source ────────────────────────────────────────────────────
    // TrovesApiService implements RemoteDatasource; Koin injects HttpClient via get()
    single<RemoteDatasource> { RemoteDatasourceImpl(get()) }

    // ── Repositories ──────────────────────────────────────────────────────────
    single<TrovesRepository>          { TrovesRepositoryImpl(get()) }
    single<AuthenticationRepository>  { AuthenticationRepositoryImpl() }
    single<PaymentRepository>         { PaymentRepositoryImpl() }

    // Home feed — fake source for now; swap for the real Shopify-backed repo later
    single<HomeRepository>            { FakeHomeRepository() }
}