package com.example.data.source.remote.service

import com.example.data.config.ShopifyConfig
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.headers

val AuthPlugin = createClientPlugin("authPlugin") {
    onRequest { request, _ ->
        request.headers {
            append("X-Shopify-Access-Token", ShopifyConfig.API_KEY)
            append("Content-Type", "application/json")
        }
    }
}