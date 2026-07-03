package com.troves.data.source.local.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor

@Database(entities = [WishlistEntity::class, AddressDecorationEntity::class, CartEntity::class], version = 6)
@ConstructedBy(TrovesDatabaseConstructor::class)
abstract class TrovesDatabase : RoomDatabase() {
    abstract fun wishlistDao(): WishlistDao
    abstract fun addressDao(): AddressDao
    abstract fun cartDao(): CartDao
}

@Suppress("KotlinNoActualForExpect")
expect object TrovesDatabaseConstructor : RoomDatabaseConstructor<TrovesDatabase> {
    override fun initialize(): TrovesDatabase
}