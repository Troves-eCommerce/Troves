package com.troves.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Query("SELECT * FROM cart ORDER BY id DESC")
    fun getAllCartItems(): Flow<List<CartEntity>>

    @Query("SELECT * FROM cart WHERE id = :productId LIMIT 1")
    suspend fun getCartItem(productId: Long): CartEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(entity: CartEntity)

    @Query("UPDATE cart SET quantity = :quantity WHERE id = :productId")
    suspend fun updateQuantity(productId: Long, quantity: Int)

    @Query("DELETE FROM cart WHERE id = :productId")
    suspend fun deleteCartItem(productId: Long)

    @Query("DELETE FROM cart")
    suspend fun clearAll()
}
