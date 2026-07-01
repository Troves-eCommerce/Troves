package com.troves.data.local.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor

@Database(entities = [WishlistEntity::class], version = 2)
@ConstructedBy(TrovesDatabaseConstructor::class)
abstract class TrovesDatabase : RoomDatabase() {
    abstract fun wishlistDao(): WishlistDao
}

@Suppress("KotlinNoActualForExpect")
expect object TrovesDatabaseConstructor : RoomDatabaseConstructor<TrovesDatabase> {
    override fun initialize(): TrovesDatabase
}