package com.troves.data.network

import com.troves.data.config.ShopifyConfig
import com.troves.data.source.remote.service.AuthPlugin
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
fun provideHttpClient(): HttpClient = HttpClient {


    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            prettyPrint = true
            isLenient = true
        })
    }
    install(Logging) {
        logger = object : Logger {
            override fun log(message: String) {
                println(message)
            }
        }

        level = LogLevel.BODY

        sanitizeHeader {
            it == HttpHeaders.Authorization
        }
    }
    install(AuthPlugin)

    defaultRequest {
        url(ShopifyConfig.REST_URL)
    }
}