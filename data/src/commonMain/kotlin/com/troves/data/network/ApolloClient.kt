package com.troves.data.network

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.network.http.LoggingInterceptor
import com.troves.data.config.ShopifyConfig

 fun provideApolloClient(): ApolloClient {
    return ApolloClient.Builder()
        .serverUrl("${ShopifyConfig.REST_URL}/graphql.json")
        .addHttpHeader("X-Shopify-Storefront-Access-Token", ShopifyConfig.API_KEY)
        .addHttpHeader("Content-Type", "application/json")
        .addHttpInterceptor(LoggingInterceptor(level = LoggingInterceptor.Level.BODY))
        .build()
}