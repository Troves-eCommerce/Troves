package com.troves.data.source.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AddressDao {
    @Query("SELECT * FROM address_decorations")
    suspend fun getDecorations(): List<AddressDecorationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDecoration(decoration: AddressDecorationEntity)

    @Query("DELETE FROM address_decorations WHERE addressId = :addressId")
    suspend fun deleteDecoration(addressId: String)
}
