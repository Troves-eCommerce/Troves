package com.troves.data.local.preferenceses

import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import okio.Path.Companion.toPath
import platform.Foundation.NSHomeDirectory

fun createDataStore(): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
        produceFile = {
            (NSHomeDirectory() + "/Documents/" + DATA_STORE_FILE_NAME).toPath()
        }
    )
