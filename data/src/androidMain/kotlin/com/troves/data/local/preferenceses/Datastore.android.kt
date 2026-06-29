package com.troves.data.local.preferenceses

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import okio.Path.Companion.toPath

// Note: Although expect/actual requires matching signatures, 
// we'll handle the Context injection at the module level in androidMain.
// To satisfy the expect fun createDataStore(): DataStore<Preferences> in commonMain,
// we'll use a trick or just provide it directly in the module.
// However, the user specifically asked for:
// androidMain: actual fun createDataStore(context: Context): DataStore<Preferences>
// But Kotlin doesn't allow changing signatures. 
// I will provide a version that works with the platform module.

fun createDataStore(context: Context): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
        produceFile = {
            context.filesDir
                .resolve(DATA_STORE_FILE_NAME)
                .absolutePath
                .toPath()
        }
    )
