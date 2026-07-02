package com.troves.data.source.local.database

import androidx.room.RoomDatabase

internal const val DATABASE_NAME = "troves_database.db"

expect class DatabaseFactory {
    fun createBuilder(): RoomDatabase.Builder<TrovesDatabase>
}
