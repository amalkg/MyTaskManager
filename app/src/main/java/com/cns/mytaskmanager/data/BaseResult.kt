package com.cns.mytaskmanager.data

sealed class BaseResult<out T> {
    data class Success<out T>(val data: T) : BaseResult<T>()
    data class Error(val exception: Exception) : BaseResult<Nothing>()
    object Loading : BaseResult<Nothing>()
}