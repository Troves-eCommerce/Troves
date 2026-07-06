package com.troves.designsystem.util

/**
 * A simple utility to strip HTML tags from a string.
 * This is useful for displaying product descriptions that might contain HTML from Shopify.
 */
fun String.stripHtml(): String {
    return this.replace(Regex("<[^>]*>"), "")
        .replace("&nbsp;", " ")
        .replace("&amp;", "&")
        .replace("&lt;", "<")
        .replace("&gt;", ">")
        .replace("&quot;", "\"")
        .replace("&apos;", "'")
        .trim()
}
