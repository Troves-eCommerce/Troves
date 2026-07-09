package com.troves.data.source.remote.service.apollo

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.troves.data.source.local.preferenceses.TrovesPreferences
import com.troves.data.source.remote.service.TrovesApiService
import com.troves.data.source.remote.service.apollo.graphql.admin.GetCollectionsQuery
import com.troves.data.source.remote.service.apollo.graphql.admin.GetProductByIdQuery
import com.troves.data.source.remote.service.apollo.graphql.admin.GetProductsByCollectionQuery
import com.troves.data.source.remote.service.apollo.graphql.admin.GetProductsBySearchQuery
import com.troves.data.source.remote.service.apollo.graphql.admin.GetProductsByVendorQuery
import com.troves.data.source.remote.service.apollo.graphql.admin.GetProductsQuery
import com.troves.data.source.remote.service.apollo.graphql.admin.GetLocalizedProductTitlesQuery
import com.troves.data.source.remote.service.apollo.graphql.admin.GetDiscountCodeQuery
import com.troves.data.source.remote.service.apollo.graphql.admin.CreateOrderMutation
import com.troves.data.source.remote.service.apollo.graphql.admin.type.OrderCreateLineItemInput
import com.troves.data.source.remote.service.apollo.graphql.admin.type.OrderCreateOrderInput
import com.troves.data.source.remote.service.apollo.graphql.admin.type.MailingAddressInput
import com.troves.data.source.remote.service.apollo.graphql.admin.type.ProductCollectionSortKeys

import com.troves.data.source.remote.service.apollo.mapper.TRANSLATION_KEY_TITLE
import com.troves.data.source.remote.service.apollo.mapper.toCustomCollectionDto
import com.troves.data.source.remote.service.apollo.mapper.toDomainProduct
import com.troves.data.source.remote.service.apollo.mapper.toProductDto
import com.troves.data.source.remote.service.apollo.mapper.toSmartCollection
import com.troves.data.source.remote.service.apollo.util.gidToLong
import com.troves.data.source.remote.service.apollo.util.runMutation
import com.troves.data.source.remote.service.apollo.util.runQuery
import com.troves.data.source.remote.service.apollo.util.toCollectionGid
import com.troves.data.source.remote.service.apollo.util.toProductGid
import com.troves.data.source.remote.service.apollo.util.toVariantGid
import com.troves.domain.entity.Address
import com.troves.data.source.remote.service.apollo.util.toQueryOptional
import com.troves.data.source.remote.service.apollo.util.toShopifySearchQuery
import com.troves.data.source.remote.service.ktor.dto.Collection
import com.troves.data.source.remote.service.ktor.dto.CollectionImage
import com.troves.data.source.remote.service.ktor.dto.CustomCollectionResponse
import com.troves.data.source.remote.service.ktor.dto.MarketingEventsResponse
import com.troves.data.source.remote.service.ktor.dto.ProductDto
import com.troves.data.source.remote.service.ktor.dto.ProductResponse
import com.troves.domain.entity.Product
import com.troves.domain.entity.ProductSearchParams
import com.troves.domain.entity.DiscountCode
import com.troves.domain.utils.Result
import kotlinx.coroutines.flow.first

class ApolloTrovesApiServiceImpl(
    private val apolloClient: ApolloClient,
    private val preferences: TrovesPreferences,
) : TrovesApiService {

    // region products
    override suspend fun createProduct(productDto: ProductDto): Result<ProductDto> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllProducts(): Result<ProductResponse> =
        apolloClient.runQuery(GetProductsQuery(first = DEFAULT_PAGE_SIZE, locale = locale())) { data ->
            ProductResponse(products = data.products.edges.map { it.node.productCard.toProductDto() })
        }

    override suspend fun getProductsByQuery(queryMap: Map<String, String>): Result<ProductResponse> =
        apolloClient.runQuery(
            GetProductsBySearchQuery(
                first = queryMap["limit"]?.toIntOrNull() ?: DEFAULT_PAGE_SIZE,
                query = queryMap.toShopifySearchQuery().toQueryOptional(),
                locale = locale(),
            )
        ) { data ->
            ProductResponse(products = data.products.edges.map { it.node.productCard.toProductDto() })
        }

    override suspend fun searchProducts(params: ProductSearchParams): Result<List<Product>> {
        val freeText = params.query?.trim().orEmpty()
        return if (freeText.containsArabic()) {
            apolloClient.runQuery(
                GetProductsBySearchQuery(
                    first = DEFAULT_PAGE_SIZE,
                    query = Optional.Absent,
                    locale = locale(),
                )
            ) { data ->
                data.products.edges.map { it.node.productCard.toDomainProduct() }.filterByTitle(freeText)
            }
        } else {
            apolloClient.runQuery(
                GetProductsBySearchQuery(
                    first = params.limit,
                    query = params.toShopifySearchQuery().toQueryOptional(),
                    locale = locale(),
                )
            ) { data ->
                data.products.edges.map { it.node.productCard.toDomainProduct() }.filterByTitle(freeText)
            }
        }
    }

    override suspend fun getProductsByVendor(vendorName: String): Result<List<Product>> =
        apolloClient.runQuery(
            GetProductsByVendorQuery(
                first = SOURCE_PRODUCTS_PAGE_SIZE,
                query = "vendor:'$vendorName'",
                locale = locale(),
            )
        ) { data ->
            data.products.edges.map { it.node.productCard.toDomainProduct() }
        }

    override suspend fun getProductsByCollection(collectionId: String): Result<List<Product>> =
        apolloClient.runQuery(
            GetProductsByCollectionQuery(
                // SAFE: Extracts the number before applying the GID prefix
                id = collectionId.sanitizeId().toCollectionGid(),
                first = SOURCE_PRODUCTS_PAGE_SIZE,
                after = Optional.Absent,
                sortKey = Optional.present(ProductCollectionSortKeys.BEST_SELLING),
                locale = locale(),
            )
        ) { data ->
            val collection = data.collection
                ?: throw NoSuchElementException("Collection not found: $collectionId")
            collection.products.edges.map { it.node.toDomainProduct() }
        }

    override suspend fun getProductImages(productId: String): Result<List<CollectionImage>> {
        TODO("Not yet implemented")
    }

    override suspend fun getProductById(productId: String): Result<Product> =
        apolloClient.runQuery(
            GetProductByIdQuery(
                // SAFE: Extracts the number before applying the GID prefix
                id = productId.sanitizeId().toProductGid(),
                locale = locale()
            )
        ) { data ->
            val product = data.product?.productCard
                ?: throw NoSuchElementException("Product not found: $productId")
            product.toDomainProduct()
        }

    override suspend fun getLocalizedProductTitles(productIds: List<String>): Result<Map<Long, String>> {
        if (productIds.isEmpty()) return Result.Success(emptyMap())
        return apolloClient.runQuery(
            GetLocalizedProductTitlesQuery(
                // SAFE: Extracts the numbers before applying the GID prefix
                ids = productIds.map { it.sanitizeId().toProductGid() },
                locale = locale()
            )
        ) { data ->
            data.nodes.mapNotNull { node ->
                val product = node?.onProduct ?: return@mapNotNull null
                val id = product.id.gidToLong() ?: return@mapNotNull null
                val title = product.translations.firstOrNull { it.key == TRANSLATION_KEY_TITLE }
                    ?.value?.takeIf { it.isNotBlank() } ?: product.title
                id to title
            }.toMap()
        }
    }

    override suspend fun updateProduct(productId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteProduct(productDto: ProductDto) {
        TODO("Not yet implemented")
    }
    // endregion

    // region brands / categories
    override suspend fun getAllBrands(): Result<Collection> =
        apolloClient.runQuery(
            GetCollectionsQuery(
                first = DEFAULT_PAGE_SIZE,
                query = Optional.present(BRANDS_QUERY),
                locale = locale(),
            )
        ) { data ->
            Collection(smartCollections = data.collections.edges.map { it.node.toSmartCollection() })
        }

    override suspend fun getCategory(): Result<CustomCollectionResponse> =
        apolloClient.runQuery(
            GetCollectionsQuery(
                first = DEFAULT_PAGE_SIZE,
                query = Optional.present(CATEGORIES_QUERY),
                locale = locale(),
            )
        ) { data ->
            CustomCollectionResponse(customCollections = data.collections.edges.map { it.node.toCustomCollectionDto() })
        }
    // endregion

    // region events
    override suspend fun getAllEventsById(eventId: String): MarketingEventsResponse {
        TODO("Not yet implemented")
    }
    // endregion

    override suspend fun getDiscountCodes(): Result<List<DiscountCode>> =
        apolloClient.runQuery(GetDiscountCodeQuery()) { data ->
            data.codeDiscountNodes.nodes.mapNotNull { node ->
                node.codeDiscount.onDiscountCodeBasic?.title?.let { title ->
                    DiscountCode(title = title)
                }
            }
        }

    override suspend fun createOrder(
        email: String?,
        address: Address,
        lineItems: List<Pair<String, Int>>,
    ): Result<String> {
        val order = OrderCreateOrderInput(
            email = Optional.presentIfNotNull(email),
            shippingAddress = Optional.present(address.toMailingAddressInput()),
            lineItems = Optional.present(
                lineItems.map { (variantId, quantity) ->
                    OrderCreateLineItemInput(
                        // SAFE: Extracts the number before applying the GID prefix
                        variantId = Optional.present(variantId.sanitizeId().toVariantGid()),
                        quantity = quantity,
                    )
                }
            ),
        )
        return apolloClient.runMutation(CreateOrderMutation(order = order)) { data ->
            data.orderCreate?.userErrors?.firstOrNull()?.let { error(it.message) }
            data.orderCreate?.order?.name ?: error("Order creation returned no order")
        }
    }

    /** Helper to guarantee we only process the numeric ID, avoiding "gid://shopify/Product/gid://..." errors */
    private fun String.sanitizeId(): String = this.substringAfterLast('/')

    /** Current app language as a Shopify locale code ("ar"/"en") for `translations(locale:)`. */
    private suspend fun locale(): String =
        preferences.selectedLanguage.first().ifBlank { DEFAULT_LOCALE }

    private fun Address.toMailingAddressInput(): MailingAddressInput = MailingAddressInput(
        address1 = Optional.presentIfNotNull(address1),
        address2 = Optional.presentIfNotNull(address2),
        city = Optional.presentIfNotNull(city),
        province = Optional.presentIfNotNull(province),
        zip = Optional.presentIfNotNull(zip),
        phone = Optional.presentIfNotNull(phone),
        firstName = Optional.presentIfNotNull(firstName),
        lastName = Optional.presentIfNotNull(lastName),
        company = Optional.presentIfNotNull(company),
        country = Optional.presentIfNotNull(country),
    )

    /** True when the text has any Arabic characters (base, supplement, extended-A blocks). */
    private fun String.containsArabic(): Boolean =
        any { it in '؀'..'ۿ' || it in 'ݐ'..'ݿ' || it in 'ࢠ'..'ࣿ' }

    /** Narrow to products whose (localized) title contains the search text; no-op when text is blank. */
    private fun List<Product>.filterByTitle(text: String): List<Product> =
        if (text.isEmpty()) this else filter { it.title.contains(text, ignoreCase = true) }

    private companion object {
        const val DEFAULT_PAGE_SIZE = 250
        const val SOURCE_PRODUCTS_PAGE_SIZE = 20
        const val BRANDS_QUERY = "collection_type:Vendor"
        const val CATEGORIES_QUERY = "collection_type:Collection"
        const val PRODUCT_TYPE_QUERY =  "collection_type:product_type"
        const val DEFAULT_LOCALE = "en"
    }
}