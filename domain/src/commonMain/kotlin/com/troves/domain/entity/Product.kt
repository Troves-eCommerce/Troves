package com.troves.domain.entity

data class Product(
    val id: Long,
    val title: String,
    val vendor: String,
    val price: String,
    val imageUrl: String?,
    val status: String
)