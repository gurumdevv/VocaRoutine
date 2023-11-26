package com.gurumlab.vocaroutine.data.source.remote

sealed interface ApiResponse<T : Any>

class ApiResultSuccess<T : Any>(val data: T) : ApiResponse<T>

class ApiResultError<T : Any>(val code: Int, val message: String) : ApiResponse<T>

class ApiResultException<T : Any>(val throwable: Throwable) : ApiResponse<T>

suspend fun <T : Any> ApiResponse<T>.onSuccess(
    block: suspend (T) -> Unit
): ApiResponse<T> = apply {
    if (this is ApiResultSuccess<T>) {
        block(data)
    }
}

suspend fun <T : Any> ApiResponse<T>.onError(
    block: suspend (code: Int, message: String) -> Unit
): ApiResponse<T> = apply {
    if (this is ApiResultError<T>) {
        block(code, message)
    }
}

suspend fun <T : Any> ApiResponse<T>.onException(
    block: suspend (throwable: Throwable) -> Unit
): ApiResponse<T> = apply {
    if (this is ApiResultException<T>) {
        block(throwable)
    }
}