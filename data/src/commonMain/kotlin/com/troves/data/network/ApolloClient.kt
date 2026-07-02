package com.troves.data.network

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.network.http.LoggingInterceptor
import com.troves.data.config.ShopifyConfig

 fun provideApolloClient(): ApolloClient {
    // REST_URL already ends with the Admin API base (…/admin/api/<version>/),
    // so the GraphQL endpoint is that base + "graphql.json".
    return ApolloClient.Builder()
        .serverUrl("${ShopifyConfig.REST_URL.trimEnd('/')}/graphql.json")
        .addHttpHeader("X-Shopify-Access-Token", ShopifyConfig.API_KEY)
        .addHttpHeader("Content-Type", "application/json")
        .addHttpInterceptor(LoggingInterceptor(level = LoggingInterceptor.Level.BODY))
        .build()
}