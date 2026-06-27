package com.example.presintation.di

import org.koin.dsl.module

/**
 * Presentation-layer Koin module.
 * ViewModels are registered here as screens are built out.
 */
val presentationModule = module {
    // ViewModels will be registered here, e.g.:
    // viewModel { ProductsViewModel(get()) }
}