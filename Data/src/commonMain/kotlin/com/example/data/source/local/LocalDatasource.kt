package com.example.data.source.local

import kotlinx.coroutines.flow.Flow

interface LocalDatasource {

    suspend fun getStringForKey(key: String): Flow<String>
    suspend fun putString(entry: Map<String, String>)


    suspend fun getIntForKey(key: String): Flow<Int>
    suspend fun putInt(entry: Map<String, Int>)


    suspend fun getBooleanForKey(key: String): Flow<Boolean>
    suspend fun putBoolean(entry: Map<String, Boolean>)


}