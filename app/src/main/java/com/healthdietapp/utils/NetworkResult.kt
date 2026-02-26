package com.healthdietapp.utils

sealed class NetworkResult<T> {
    class Success<T>(val data: T) : NetworkResult<T>()
    class Error<T>(val message: String, val code: Int? = null) : NetworkResult<T>()
    class Loading<T> : NetworkResult<T>()
}
