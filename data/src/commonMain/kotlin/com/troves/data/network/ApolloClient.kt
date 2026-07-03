package com.troves.data.network

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.network.http.LoggingInterceptor
import com.troves.data.config.ShopifyConfig

 fun provideAdminApolloClient(): ApolloClient {
    return ApolloClient.Builder()
        .serverUrl("${ShopifyConfig.REST_URL.trimEnd('/')}/graphql.json")
        .addHttpHeader("X-Shopify-Access-Token", ShopifyConfig.API_KEY)
        .addHttpHeader("Content-Type", "application/json")
        .addHttpInterceptor(LoggingInterceptor(level = LoggingInterceptor.Level.BODY))
        .build()
}
fun provideStorefrontApolloClient(): ApolloClient {
    return ApolloClient.Builder()
        .serverUrl("${ShopifyConfig.STOREFRONT_URL.trimEnd('/')}/graphql.json")
        .addHttpHeader("X-Shopify-Storefront-Access-Token", ShopifyConfig.STOREFRONT_ACCESS_TOKEN)
        .addHttpHeader("Content-Type", "application/json")
        .addHttpInterceptor(LoggingInterceptor(level = LoggingInterceptor.Level.BODY))
        .build()
}