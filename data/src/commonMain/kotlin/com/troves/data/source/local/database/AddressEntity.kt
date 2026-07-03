package com.troves.data.source.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "addresses")
data class AddressEntity(
    @PrimaryKey
    val id: String,
    val label: String,
    val icon: String, // Serialize enum to string
    val phone: String,
    val lines: String, // Serialize List<String> to a single string (e.g. joined by newline)
    val isDefault: Boolean,
    val note: String? = null
)
