package com.troves.data.source.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wishlist")
data class WishlistEntity(
    @PrimaryKey val id: Long,
    val title: String,
    val vendor: String,
    val price: String,
    val imageUrl: String,
    val status: String,
)