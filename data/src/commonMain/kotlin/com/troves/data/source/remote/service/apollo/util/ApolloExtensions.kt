package com.troves.data.source.remote.service.apollo.util

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.ApolloResponse
import com.apollographql.apollo.api.Mutation
import com.apollographql.apollo.api.Operation
import com.apollographql.apollo.api.Optional
import com.apollographql.apollo.api.Query
import com.troves.domain.utils.Result



internal suspend fun <D : Query.Data, T> ApolloClient.runQuery(
    operation: Query<D>,
    transform: (D) -> T,
): Result<T> = try {
    query(operation).execute().toResult(transform)
} catch (e: Exception) {
    Result.Error(e)
}

internal suspend fun <D : Mutation.Data, T> ApolloClient.runMutation(
    operation: Mutation<D>,
    transform: (D) -> T,
): Result<T> = try {
    mutation(operation).execute().toResult(transform)
} catch (e: Exception) {
    Result.Error(e)
}

internal fun <D : Operation.Data, T> ApolloResponse<D>.toResult(transform: (D) -> T): Result<T> {
    graphqlErrorOrNull()?.let { return it }
    val data = data ?: return Result.Error(IllegalStateException("Empty GraphQL response"))
    return Result.Success(transform(data))
}

internal fun ApolloResponse<*>.graphqlErrorOrNull(): Result.Error? =
    if (hasErrors()) {
        Result.Error(Exception(errors?.firstOrNull()?.message ?: "Unknown GraphQL Error"))
    } else {
        null
    }

internal fun String.toQueryOptional(): Optional<String?> =
    Optional.presentIfNotNull(ifBlank { null })


internal fun String.gidToLong(): Long? = substringAfterLast('/').toLongOrNull()

internal fun String.toProductGid(): String =
    if (startsWith("gid://")) this else "gid://shopify/Product/$this"

internal fun String.toCollectionGid(): String =
    if (startsWith("gid://")) this else "gid://shopify/Collection/$this"

internal fun String.toVariantGid(): String =
    if (startsWith("gid://")) this else "gid://shopify/ProductVariant/$this"
