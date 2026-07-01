package com.troves.domain.utils

/**
 * Copyright (c) 2026 Wahid Ali Wahid Hussien.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Author: Wahid Ali Wahid Hussien
 * Created: 30/06/2026
 */

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