package com.example.data.source.local.datasorce.preferenceses

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import okio.Path.Companion.toPath

actual fun createDataStore(producePath: () -> String): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
        produceFile = { producePath().toPath() }
    )


fun createDataStoreAndroid(context: Context): DataStore<Preferences> =
    createDataStore {
        context.filesDir
            .resolve(DATA_STORE_FILE_NAME)
            .absolutePath
    }