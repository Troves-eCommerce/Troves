package com.troves.domain

interface TrovesRepository {
    suspend fun getAllProducts(): Result<List<Product>>
}