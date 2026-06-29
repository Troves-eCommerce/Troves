package com.troves.data.di

import com.troves.data.network.provideHttpClient
import com.troves.data.repository.AuthenticationRepositoryImpl
import com.troves.data.repository.PaymentRepositoryImpl
import com.troves.data.repository.TrovesRepositoryImpl
import com.troves.data.source.remote.RemoteDatasource
import com.troves.data.source.remote.RemoteDatasourceImpl
import com.troves.data.source.remote.service.TrovesApiService
import com.troves.data.source.remote.service.TrovesApiServiceImpl
import com.troves.domain.AuthenticationRepository
import com.troves.domain.repository.PaymentRepository
import com.troves.domain.repository.TrovesRepository
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
}