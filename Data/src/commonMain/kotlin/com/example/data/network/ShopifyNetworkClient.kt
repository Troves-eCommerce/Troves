package com.example.data.network

import com.example.data.config.ShopifyConfig
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

private const val API_VERSION = "2024-01"

/**
 * Factory function that constructs a fully configured [HttpClient].
 * Called by Koin — credentials are read from [ShopifyConfig] which is
 * backed by build-time constants from local.properties (never in VCS).
 */
fun provideHttpClient(): HttpClient = HttpClient {

    install(Auth) {
        basic {
            credentials {
                BasicAuthCredentials(
                    username = ShopifyConfig.API_KEY,
                    password = ShopifyConfig.PASSWORD
                )
            }
            sendWithoutRequest { true } // يرسل بيانات الدخول فوراً لتوفير الوقت
        }
    }

    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true // تجاهل أي بيانات يرسلها Shopify ولا نحتاجها
            prettyPrint = true
            isLenient = true
        })
    }

    defaultRequest {
        url {
            protocol = URLProtocol.HTTPS
            host = ShopifyConfig.HOSTNAME
            path("admin/api/$API_VERSION/")
        }
        contentType(ContentType.Application.Json)
    }
}