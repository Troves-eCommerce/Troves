package com.troves.data.source.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "address_decorations")
data class AddressDecorationEntity(
    @PrimaryKey
    val addressId: String,
    val label: String?,
    val icon: String,
    val note: String?,
)
