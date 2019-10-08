package com.textaddict.app.network

sealed class Output<out T : Any> {
    data class Success<out T : Any>(val output: T) : Output<T>()
    data class Error(val exception: Exception, val messageOut: String) : Output<Nothing>()
}

sealed class ResultLogin {
    object Success : ResultLogin()
    class Error(val message: String, val exception: Exception) : ResultLogin()
}