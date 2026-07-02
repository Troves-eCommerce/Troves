package com.troves.data.local.database

import androidx.room.Room
import androidx.room.RoomDatabase
import platform.Foundation.NSHomeDirectory

actual class DatabaseFactory {
    actual fun createBuilder(): RoomDatabase.Builder<TrovesDatabase> {
        val dbFilePath = NSHomeDirectory() + "/Documents/" + DATABASE_NAME
        return Room.databaseBuilder<TrovesDatabase>(
            name = dbFilePath,
            factory = { TrovesDatabase::class.instantiateImpl() }
        ).fallbackToDestructiveMigration(true)
    }
}
