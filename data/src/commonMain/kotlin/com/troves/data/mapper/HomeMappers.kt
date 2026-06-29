package com.troves.data.mapper

import com.troves.data.source.remote.dto.CustomCollectionDto
import com.troves.data.source.remote.dto.ProductDto
import com.troves.data.source.remote.dto.SmartCollection
import com.troves.domain.entity.Product
import com.troves.domain.entity.Brand
import com.troves.domain.entity.Category


fun ProductDto.toDomain(): Product = Product(
    id = id ?: 0L,
    title = title.orEmpty(),
    vendor = vendor.orEmpty(),
    price = variants?.firstOrNull()?.price.orEmpty(),
    imageUrl = image?.src,
    status = status.orEmpty(),
    images = images?.map { it?.src ?: "" }.orEmpty(),
    sizes = options
        ?.firstOrNull { it?.name?.lowercase() == "size" }
        ?.values
        ?.filterNotNull()
        ?: emptyList(),
    colors = options
        ?.firstOrNull { it?.name?.lowercase() == "color" }
        ?.values
        ?.filterNotNull()
        ?: emptyList()
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