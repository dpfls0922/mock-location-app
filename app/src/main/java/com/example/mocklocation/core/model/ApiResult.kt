package com.example.mocklocation.core.model

sealed class ApiResult<out T> {
    data class Success<out T>(val data: T): ApiResult<T>()
    data class Error(val message: String): ApiResult<Nothing>()
}

inline fun <T, R> ApiResult<T>.mapSuccess(transform: (T) -> R): ApiResult<R> = when(this) {
    is ApiResult.Success -> ApiResult.Success(transform(this.data))
    is ApiResult.Error -> this
}