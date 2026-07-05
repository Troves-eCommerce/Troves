package com.troves.data.util

import platform.Foundation.NSUserDefaults


actual fun applyAppLocale(languageCode: String) {
    NSUserDefaults.standardUserDefaults.setObject(
        listOf(languageCode),
        forKey = "AppleLanguages"
    )
    NSUserDefaults.standardUserDefaults.synchronize()
}