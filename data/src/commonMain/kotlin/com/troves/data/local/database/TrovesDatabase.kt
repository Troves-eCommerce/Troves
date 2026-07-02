package com.troves.data.local.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor

@Database(entities = [WishlistEntity::class, AddressEntity::class], version = 4)
@ConstructedBy(TrovesDatabaseConstructor::class)
abstract class TrovesDatabase : RoomDatabase() {
    abstract fun wishlistDao(): WishlistDao
    abstract fun addressDao(): AddressDao
}

@Suppress("KotlinNoActualForExpect")
expect object TrovesDatabaseConstructor : RoomDatabaseConstructor<TrovesDatabase> {
    override fun initialize(): TrovesDatabase
}