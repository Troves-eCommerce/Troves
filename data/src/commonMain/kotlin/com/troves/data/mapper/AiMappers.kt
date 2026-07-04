package com.troves.data.mapper

import com.troves.data.source.remote.ai.dto.AiChatResponseDto
import com.troves.data.source.remote.ai.dto.AiProductDto
import com.troves.domain.entity.AiChatReply
import com.troves.domain.entity.AiRecommendedProduct

fun AiChatResponseDto.toDomain(): AiChatReply = AiChatReply(
    reasoning = reasoning,
    clarifyingQuestion = clarifyingQuestion,
    noMatch = noMatch,
    declined = declined,
    suggested = suggested,
    products = products.map { it.toDomain() },
)

fun AiProductDto.toDomain(): AiRecommendedProduct = AiRecommendedProduct(
    id = id.orEmpty(),
    handle = handle.orEmpty(),
    title = title.orEmpty(),
    description = description.orEmpty(),
    imageUrl = featuredImage,
    priceFormatted = price?.formatted ?: price?.amount.orEmpty(),
    priceAmount = price?.amount.orEmpty(),
    available = available,
    why = why,
)
