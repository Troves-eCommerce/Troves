package com.troves.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
@Dao
interface WishlistDao {
    @Query("SELECT * FROM wishlist ORDER BY id DESC")
    fun getAllFavorites(): Flow<List<WishlistEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM wishlist WHERE id = :productId)")
    fun isFavorite(productId: Long): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(entity: WishlistEntity)

    @Query("DELETE FROM wishlist WHERE id = :productId")
    suspend fun deleteFavorite(productId: Long)
}