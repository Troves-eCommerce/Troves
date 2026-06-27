package com.example.domain

sealed interface Result<out R> {
    data class Success<out T>(val value: T) : Result<T>
    data object Loading : Result<Nothing>
    class Error(val throwable: Throwable) : Result<Nothing>
}


inline fun <T, R> Result<T>.map(transform: (value:T) -> R): Result<R> =
     when (this) {
        is Result.Loading -> Result.Loading
        is Result.Success -> Result.Success(transform(value))
        is Result.Error -> Result.Error(throwable)
    }

fun <T> Result<T>.getOrNull(): T? =
    if (this is Result.Success) value else null

fun <T> Result<T>.getOrElse(default: T): T =
    if (this is Result.Success) value else default

fun <T> Result<T>.getOrElse(default: () -> T): T =
    if (this is Result.Success) value else default()

fun <T> Result<T>.getOrThrow(): T = when (this) {
    is Result.Success -> value
    is Result.Error   -> throw throwable
    is Result.Loading -> throw IllegalStateException("Result is still Loading")
}

fun <T, R> Result<T>.fold(
    onSuccess: (T) -> R,
    onError:   (Throwable) -> R,
    onLoading: () -> R,
): R = when (this) {
    is Result.Success -> onSuccess(value)
    is Result.Error   -> onError(throwable)
    is Result.Loading -> onLoading()
}