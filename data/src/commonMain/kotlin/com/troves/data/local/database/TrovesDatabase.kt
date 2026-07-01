package com.troves.data.local.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor

@Database(entities = [WishlistEntity::class, CartEntity::class], version = 3)
@ConstructedBy(TrovesDatabaseConstructor::class)
abstract class TrovesDatabase : RoomDatabase() {
    abstract fun wishlistDao(): WishlistDao
    abstract fun cartDao(): CartDao
}

@Suppress("KotlinNoActualForExpect")
expect object TrovesDatabaseConstructor : RoomDatabaseConstructor<TrovesDatabase> {
    override fun initialize(): TrovesDatabase
}