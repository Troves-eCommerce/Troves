package com.example.presintation.di

import com.example.presintation.products.ProductsViewModel
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    // ViewModels — koin-compose-viewmodel handles lifecycle automatically
    viewModel { ProductsViewModel(get()) }
}