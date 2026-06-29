package com.troves.data.config

import com.example.data.BuildKonfig


/**
 * Shopify API credentials loaded from local.properties at build time via BuildKonfig.
 * The actual values are NEVER stored in source code or version control.
 */
object ShopifyConfig {
    val API_KEY: String   = BuildKonfig.SHOPIFY_API_KEY
    val REST_URL: String  = BuildKonfig.SHOPIFY_REST_URL
}
