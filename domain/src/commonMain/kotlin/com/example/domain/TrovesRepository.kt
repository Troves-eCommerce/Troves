package com.example.domain

interface TrovesRepository {
    suspend fun getAllProducts(): Result<List<Product>>
}