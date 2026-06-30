package com.troves.presintation.ui.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troves.domain.Result
import com.troves.domain.getOrElse
import com.troves.domain.usecase.home.GetCategoriesUseCase
import com.troves.domain.usecase.products.FilterProductsUseCase
import com.troves.domain.usecase.products.SortProductsUseCase
import com.troves.domain.usecase.shared.GetProductsUseCase
import com.troves.presintation.core.mvi.DefaultEffectPublisher
import com.troves.presintation.core.mvi.DefaultStateHolder
import com.troves.presintation.core.mvi.EffectPublisher
import com.troves.presintation.core.mvi.StateHolder
import com.troves.presintation.ui.components.FilterOption
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ProductsViewModel(
    private val getProducts: GetProductsUseCase,
    private val getCategories: GetCategoriesUseCase,
    private val filterProducts: FilterProductsUseCase,
    private val sortProducts: SortProductsUseCase,
) : ViewModel(),
    StateHolder<ProductsUiState> by DefaultStateHolder(ProductsUiState()),
    EffectPublisher<ProductsEffect> by DefaultEffectPublisher() {

    init {
        onIntent(ProductsIntent.Load)
    }

    fun onIntent(intent: ProductsIntent) {
        when (intent) {
            ProductsIntent.Load, ProductsIntent.Retry -> loadProducts()

            ProductsIntent.OnBackClick -> {
                updateState {
                    copy(
                        draftCategoryIds = emptySet(),
                        draftSubCategoryIds = emptySet(),
                        draftBrandIds = emptySet(),

                    )
                }
                sendEffect(ProductsEffect.NavigateBack)

            }

            ProductsIntent.OpenFilter -> updateState {
                copy(
                    showFilterSheet = true,
                    draftCategoryIds = selectedCategoryIds,
                    draftSubCategoryIds = selectedSubCategoryIds,
                    draftBrandIds = selectedBrandIds,
                )
            }
            ProductsIntent.OpenSort -> updateState {
                copy(showSortSheet = true, draftSort = selectedSort)
            }
            ProductsIntent.DismissSheet -> updateState {
                copy(showFilterSheet = false, showSortSheet = false)
            }

            is ProductsIntent.ToggleCategory -> updateState {
                copy(draftCategoryIds = draftCategoryIds.toggle(intent.id))
            }
            is ProductsIntent.ToggleSubCategory -> updateState {
                copy(draftSubCategoryIds = draftSubCategoryIds.toggle(intent.id))
            }
            is ProductsIntent.ToggleBrand -> updateState {
                copy(draftBrandIds = draftBrandIds.toggle(intent.id))
            }

            ProductsIntent.ApplyFilter -> updateState {
                copy(
                    selectedCategoryIds = draftCategoryIds,
                    selectedSubCategoryIds = draftSubCategoryIds,
                    selectedBrandIds = draftBrandIds,
                    showFilterSheet = false,
                ).withDisplayedProducts()
            }
            ProductsIntent.ResetFilter -> updateState {
                copy(
                    draftCategoryIds = emptySet(),
                    draftSubCategoryIds = emptySet(),
                    draftBrandIds = emptySet(),
                )
            }

            is ProductsIntent.SelectSort -> updateState { copy(draftSort = intent.option) }
            ProductsIntent.ApplySort -> updateState {
                copy(selectedSort = draftSort, showSortSheet = false).withDisplayedProducts()
            }

            is ProductsIntent.ProductClicked ->
                sendEffect(ProductsEffect.NavigateToProduct(intent.product.id.toString()))
        }
    }

    private fun loadProducts() {
        viewModelScope.launch {
            updateState { copy(isLoading = true, errorMessage = null) }

            val productsDeferred = async { getProducts() }
            val categoriesDeferred = async { getCategories() }

            val productsResult = productsDeferred.await()
            val categories = categoriesDeferred.await().getOrElse(emptyList())

            when (productsResult) {
                is Result.Success -> {
                    val products = productsResult.value
                    updateState {
                        copy(
                            isLoading = false,
                            errorMessage = null,
                            allProducts = products,
                            categoryOptions = categories.map {
                                FilterOption(id = it.id.toString(), label = it.name)
                            },
                            brandOptions = products
                                .map { it.vendor }
                                .filter { it.isNotBlank() }
                                .distinct()
                                .map { FilterOption(id = it, label = it) },
                        ).withDisplayedProducts()
                    }
                }

                is Result.Error -> updateState {
                    copy(
                        isLoading = false,
                        errorMessage = productsResult.throwable.message ?: "Something went wrong",
                    )
                }

                is Result.Loading -> Unit
            }
        }
    }

    private fun ProductsUiState.withDisplayedProducts(): ProductsUiState {
        val categoryNames = categoryOptions
            .filter { it.id in selectedCategoryIds }
            .map { it.label }
            .toSet()

        val filtered = filterProducts(
            products = allProducts,
            brandIds = selectedBrandIds,
            categoryNames = categoryNames,
        )

        return copy(displayedProducts = sortProducts(filtered, selectedSort.criteria))
    }

    private fun Set<String>.toggle(id: String): Set<String> =
        if (id in this) this - id else this + id
}
