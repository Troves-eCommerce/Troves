package com.troves.data.config

import com.troves.data.BuildKonfig


/**
 * Shopify API credentials loaded from local.properties at build time via BuildKonfig.
 * The actual values are NEVER stored in source code or version control.
 */
object ShopifyConfig {
    val API_KEY: String   = BuildKonfig.SHOPIFY_API_KEY
    val PASSWORD: String  = BuildKonfig.SHOPIFY_PASSWORD
    val HOSTNAME: String  = BuildKonfig.SHOPIFY_HOSTNAME
}
