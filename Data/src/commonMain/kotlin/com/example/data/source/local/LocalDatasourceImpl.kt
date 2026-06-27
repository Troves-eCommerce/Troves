package com.example.data.source.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.data.source.local.datasorce.preferenceses.Datastore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalDatasourceImpl(
    private val datastore: DataStore<Preferences> = Datastore.datastore
) : LocalDatasource {
    override suspend fun getStringForKey(key: String): Flow<String> {
        TODO("Not yet implemented")
    }

    override suspend fun putString(entry: Map<String, String>) {
        TODO("Not yet implemented")
    }

    override suspend fun getIntForKey(key: String): Flow<Int> {
        TODO("Not yet implemented")
    }

    override suspend fun putInt(entry: Map<String, Int>) {
        TODO("Not yet implemented")
    }

    override suspend fun getBooleanForKey(key: String): Flow<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun putBoolean(entry: Map<String, Boolean>) {
        TODO("Not yet implemented")
    }
}