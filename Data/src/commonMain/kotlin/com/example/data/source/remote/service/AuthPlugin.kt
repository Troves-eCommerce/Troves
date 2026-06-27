package com.example.data.source.remote.service

import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.headers

val AuthPlugin = createClientPlugin("authPlugin"){
    onRequest { request, content ->
        request.headers {
            append("X-Shopify-Access-Token","API-KEY")
            append("Content-Type","application/json")
        }
    }
}