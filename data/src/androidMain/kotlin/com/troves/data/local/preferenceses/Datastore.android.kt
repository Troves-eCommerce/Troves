package com.troves.data.local.preferenceses

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import com.troves.data.source.local.preferenceses.DATA_STORE_FILE_NAME
import okio.Path.Companion.toPath

// Android needs a Context to locate filesDir, so the platform Koin module
// supplies it (see androidMain platformModule). iOS provides its own no-arg
// createDataStore() using NSHomeDirectory. Both back the same shared
// AppPreferencesDataSource in commonMain.
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
