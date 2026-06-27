package com.example.data.source.local.datasorce.preferenceses

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences


object Datastore {
    val datastore = createDataStore { DATA_STORE_FILE_NAME }
}

internal const val DATA_STORE_FILE_NAME = "app_preferences.preferences_pb"

// Each platform supplies its own file path
expect fun createDataStore(producePath: () -> String): DataStore<Preferences>