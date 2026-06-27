package com.example.data.network


import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import com.example.data.config.ShopifyConfig

object ShopifyNetworkClient {

    private val API_KEY = ShopifyConfig.API_KEY
    private val PASSWORD = ShopifyConfig.PASSWORD
    private val HOSTNAME = ShopifyConfig.HOSTNAME
    private const val API_VERSION = "2024-01"

    val httpClient = HttpClient {

        install(Auth) {
            basic {
                credentials {
                    BasicAuthCredentials(username = API_KEY, password = PASSWORD)
                }
                sendWithoutRequest { true }
            }
        }

        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }

        defaultRequest {
            url {
                protocol = URLProtocol.HTTPS
                host = HOSTNAME
                path("admin/api/$API_VERSION/")
            }
            contentType(ContentType.Application.Json)
        }
    }
}