package com.troves.data.local.database

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.troves.data.source.local.database.DATABASE_NAME
import com.troves.data.source.local.database.TrovesDatabase
import kotlinx.coroutines.Dispatchers
import platform.Foundation.NSHomeDirectory

actual class DatabaseFactory {
    actual fun createBuilder(): RoomDatabase.Builder<TrovesDatabase> {
        val dbFilePath = NSHomeDirectory() + "/Documents/" + DATABASE_NAME
        return Room.databaseBuilder<TrovesDatabase>(
            name = dbFilePath
        )
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.Default)
    }
}
