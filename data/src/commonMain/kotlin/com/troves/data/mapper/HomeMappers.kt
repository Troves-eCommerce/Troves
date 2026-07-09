package com.troves.data.mapper

import com.troves.data.source.remote.service.ktor.dto.CustomCollectionDto
import com.troves.data.source.remote.service.ktor.dto.ProductDto
import com.troves.data.source.remote.service.ktor.dto.SmartCollection
import com.troves.domain.entity.Product
import com.troves.domain.entity.Brand
import com.troves.domain.entity.Category
import kotlin.random.Random


fun ProductDto.toDomain(): Product = Product(
    id = id ?: 0L,
    title = title.orEmpty(),
    vendor = vendor.orEmpty(),
    price = variants?.firstOrNull()?.price.orEmpty(),
    imageUrl = image?.src?: "",
    status = status.orEmpty(),
    images = images?.map { it?.src ?: "" }.orEmpty(),
    sizes = options?.firstOrNull { it?.name.equals("Size", ignoreCase = true) }
        ?.values
        ?.filterNotNull()
        ?: emptyList(),
    colors = options
        ?.firstOrNull { it?.name.equals("Color", ignoreCase = true) }
        ?.values
        ?.filterNotNull()
        ?: emptyList(),
    description = bodyHtml ?: "",
    rating = Random.nextInt(3,5)
)

fun SmartCollection.toBrand(): Brand = Brand(
    id = id ?: 0L,
    name = title.orEmpty(),
    logoUrl = collectionImage?.src,
)

fun CustomCollectionDto.toCategory(): Category = Category(
    id = id ?: 0L,
    name = title.orEmpty(),
    imageUrl = customCollectionImage?.src,
)