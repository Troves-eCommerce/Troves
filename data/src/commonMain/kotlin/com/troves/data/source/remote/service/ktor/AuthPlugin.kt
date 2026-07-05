package com.troves.data.source.remote.service.ktor

import com.troves.data.config.ShopifyConfig
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.headers

val AuthPlugin = createClientPlugin("authPlugin") {
    onRequest { request, _ ->
        request.headers {
            append("X-Shopify-Access-Token", ShopifyConfig.API_KEY)
        }
    }
}

val PaymobPlugin = createClientPlugin("PaymobPlugin"){
    onRequest { request, content ->
        request.headers{
            append("apikey",ShopifyConfig.SUPABASE_API_KEY)
            append("Authorization","Bearer ${ShopifyConfig.SUPABASE_API_KEY}")
            append("Content-Type","application/json")
        }
    }
}