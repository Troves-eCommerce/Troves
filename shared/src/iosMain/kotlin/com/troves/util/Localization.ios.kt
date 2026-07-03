package com.troves.util

import platform.Foundation.NSUserDefaults

actual fun changeLocale(languageCode: String) {
    val array = listOf(languageCode)
    NSUserDefaults.standardUserDefaults.setObject(array, "AppleLanguages")
}
