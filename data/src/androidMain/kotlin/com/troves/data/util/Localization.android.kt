package com.troves.data.util

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat


actual fun applyAppLocale(languageCode: String) {
    AppCompatDelegate.setApplicationLocales(
        LocaleListCompat.forLanguageTags(languageCode)
    )
}
