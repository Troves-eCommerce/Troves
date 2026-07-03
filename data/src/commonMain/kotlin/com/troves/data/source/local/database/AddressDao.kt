package com.troves.data.source.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AddressDao {
    @Query("SELECT * FROM addresses")
    fun getAddresses(): Flow<List<AddressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddress(address: AddressEntity)

    @Query("DELETE FROM addresses WHERE id = :addressId")
    suspend fun deleteAddress(addressId: String)
    
    @Update
    suspend fun updateAddress(address: AddressEntity)
}
