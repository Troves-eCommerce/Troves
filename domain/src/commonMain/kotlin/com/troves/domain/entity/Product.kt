package com.troves.domain.entity

data class Product(
    val id: Long,
    val title: String,
    val vendor: String,
    val price: String,
    val imageUrl: String,
    val status: String,
    val images: List<String> = emptyList(),
    val sizes: List<String> = emptyList(),
    val colors: List<String> = emptyList(),
    val reviews: List<String> = emptyList(),
    val description: String = "",
    val rating: Int = 0,
)