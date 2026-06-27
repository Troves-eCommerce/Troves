package com.example.data.di

import com.example.data.network.provideHttpClient
import com.example.data.repository.AuthenticationRepositoryImpl
import com.example.data.repository.PaymentRepositoryImpl
import com.example.data.repository.TrovesRepositoryImpl
import com.example.data.source.remote.RemoteDatasource
import com.example.data.source.remote.service.TrovesApiService
import com.example.domain.AuthenticationRepository
import com.example.domain.PaymentRepository
import com.example.domain.TrovesRepository
import io.ktor.client.HttpClient
import org.koin.dsl.module

val dataModule = module {

    // ── Network ───────────────────────────────────────────────────────────────
    // single<T> means one shared instance for the entire app lifetime
    single<HttpClient> { provideHttpClient() }

    // ── Remote data source ────────────────────────────────────────────────────
    // TrovesApiService implements RemoteDatasource; Koin injects HttpClient via get()
    single<RemoteDatasource> { TrovesApiService(get()) }

    // ── Repositories ──────────────────────────────────────────────────────────
    single<TrovesRepository>          { TrovesRepositoryImpl(get()) }
    single<AuthenticationRepository>  { AuthenticationRepositoryImpl() }
    single<PaymentRepository>         { PaymentRepositoryImpl() }
}